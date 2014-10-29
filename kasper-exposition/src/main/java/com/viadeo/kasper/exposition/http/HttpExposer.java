// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public abstract class HttpExposer<INPUT, RESPONSE extends KasperResponse> extends HttpServlet {

	private static final long serialVersionUID = 8448984922303895424L;

    protected static final Logger LOGGER = LoggerFactory.getLogger(HttpExposer.class);

    private final HttpContextDeserializer contextDeserializer;
    private final AliasRegistry aliasRegistry;
    private final Logger requestLogger;
    private final MetricNames metricNames;
    private final Meta meta;

    private Optional<String> serverName = Optional.absent();

    // ------------------------------------------------------------------------

    protected HttpExposer(final HttpContextDeserializer contextDeserializer, final Meta meta) {
        this.meta = meta;
        this.metricNames = new MetricNames(getClass());
        this.contextDeserializer = checkNotNull(contextDeserializer);
        this.aliasRegistry = new AliasRegistry();
        this.serverName = Optional.absent();
        this.requestLogger = LoggerFactory.getLogger(getClass());
    }

    // ------------------------------------------------------------------------

    protected abstract RESPONSE createErrorResponse(final CoreReasonCode code, final List<String> reasons);

    protected abstract RESPONSE createRefusedResponse(final CoreReasonCode code, final List<String> reasons);

    protected abstract boolean isManageable(final String requestName);

    protected abstract <T extends INPUT> Class<T> getInputClass(final String inputName);

    public abstract RESPONSE doHandle(final INPUT input, final Context context) throws Exception;

    // ------------------------------------------------------------------------

    protected void checkMediaType(final HttpServletRequest httpRequest) throws HttpExposerException {
        // nothing
    }

    public final void handleRequest(
            final HttpServletRequestToObject requestToObject,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse
    ) throws IOException {
        final Timer.Context timer = getMetricRegistry().timer(metricNames.getRequestsTimeName()).time();

        try {
            this.doHandleRequest(requestToObject, objectToHttpResponse, httpRequest, httpResponse);
        } finally {
            timer.stop();
            getMetricRegistry().meter(metricNames.getRequestsName()).mark();
        }
    }

    protected void doHandleRequest(
            final HttpServletRequestToObject requestToObject,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse
    ) throws IOException {

        INPUT input = null;
        RESPONSE response;

        /* 0) Create a request correlation id */
        final UUID kasperCorrelationUUID = UUID.randomUUID();

        try {

            /* 1) Check that we support the requested media type*/
            checkMediaType(httpRequest);

            /* 2) Extract the context from request */
            final Context context = extractContext(httpRequest, kasperCorrelationUUID);

            /* 3) Extract the input from request */
            input = extractInput(httpRequest, requestToObject);

            enrichContextAndMDC(context, "appRoute", input.getClass().getName());

            /* 4) Handle the request */
            response = handle(input, context);

        } catch (HttpExposerException exposerException) {
            sendError(
                    httpResponse,
                    objectToHttpResponse,
                    createErrorResponse(exposerException.getCoreReasonCode(), Lists.newArrayList(exposerException.getMessage())),
                    kasperCorrelationUUID,
                    exposerException,
                    Optional.fromNullable(input)
            );
            return;

        } catch (final JSR303ViolationException validationException) {
            final List<String> errorMessages = new ArrayList<>();
            for (final ConstraintViolation<Object> violation : validationException.getViolations()) {
                errorMessages.add(violation.getPropertyPath() + " : " + violation.getMessage());
            }

            sendError(
                    httpResponse,
                    objectToHttpResponse,
                    createRefusedResponse(CoreReasonCode.INVALID_INPUT, errorMessages),
                    kasperCorrelationUUID,
                    validationException,
                    Optional.fromNullable(input)
            );
            return;

        } catch (final IOException e) {
            sendError(
                    httpResponse,
                    objectToHttpResponse,
                    createErrorResponse(
                            CoreReasonCode.INVALID_INPUT,
                            Lists.newArrayList((null == e.getMessage()) ? "Unknown" : e.getMessage())
                    ),
                    kasperCorrelationUUID,
                    e,
                    Optional.fromNullable(input)
            );
            return;

        } catch (final Throwable th) {
            sendError(
                    httpResponse,
                    objectToHttpResponse,
                    createErrorResponse(
                            CoreReasonCode.UNKNOWN_REASON,
                            Lists.newArrayList((null == th.getMessage()) ? "Unknown" : th.getMessage())
                    ),
                    kasperCorrelationUUID,
                    th,
                    Optional.fromNullable(input)
            );
            return;
        }

        try {

            /* 5) Respond to the request */
            sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);

            final String inputName = input.getClass().getSimpleName();
            requestLogger.info("Request processed in {} [{}] : {}", getInputTypeName(), inputName);

        } catch (final JsonGenerationException | JsonMappingException e) {
            sendError(
                    httpResponse,
                    objectToHttpResponse,
                    createErrorResponse(CoreReasonCode.UNKNOWN_REASON, Lists.newArrayList(String.format(
                            "Error outputting response to JSON for command [%s] and response [%s]error = %s",
                            input.getClass().getSimpleName(),
                            response,
                            e
                    ))),
                    kasperCorrelationUUID,
                    e,
                    Optional.fromNullable(input)
            );
        }
    }

    public final RESPONSE handle(final INPUT input, final Context context) throws Exception {
        final Timer.Context inputHandleTime = getMetricRegistry().timer(name(input.getClass(), "requests-handle-time")).time();
        final Timer.Context globalInputHandleTime = getMetricRegistry().timer(metricNames.getRequestsHandleTimeName()).time();

        try {
            return doHandle(input, context);
        } finally {
            inputHandleTime.stop();
            globalInputHandleTime.stop();
        }
    }

    protected void flushBuffer(final HttpServletResponse httpResponse){
        /*
         * must be last call to ensure that everything is sent to the client
         *(even if an error occurred)
         */
        try {
            httpResponse.flushBuffer();
        } catch (final IOException e) {
            requestLogger.warn("Error when trying to flush output buffer", e);
        }
    }

    protected Context extractContext(
            final HttpServletRequest httpRequest,
            final UUID kasperCorrelationUUID
    ) throws IOException {
        final Context context = contextDeserializer.deserialize(httpRequest, kasperCorrelationUUID);

        MDC.setContextMap(context.asMap());

        enrichContextAndMDC(context, "appServer", serverName());
        enrichContextAndMDC(context, "appVersion", meta.getVersion());
        enrichContextAndMDC(context, "appBuildingDate", meta.getBuildingDate().toString());
        enrichContextAndMDC(context, "appDeploymentDate", meta.getDeploymentDate().toString());
        enrichContextAndMDC(context, "clientVersion", Objects.firstNonNull(httpRequest.getHeader(HttpContextHeaders.HEADER_CLIENT_VERSION), "undefined"));
        enrichContextAndMDC(context, "clientId", Objects.firstNonNull(context.getApplicationId(), "undefined"));

        return context;
    }

    private void enrichContextAndMDC(final Context context, final String key, final String value) {
        MDC.put(key, value);
        context.setProperty(key, value);
    }

    protected INPUT extractInput(
            final HttpServletRequest httpRequest,
            final HttpServletRequestToObject httpRequestToObject
    ) throws HttpExposerException, IOException {

        /* 1) Resolve the input name */
        final String requestName = aliasRegistry.resolve(resourceName(httpRequest.getRequestURI()));

        /* 2) Check that the request is manageable*/
        if( ! isManageable(requestName)){
            throw new HttpExposerException(
                    CoreReasonCode.NOT_FOUND,
                    getInputTypeName() + "[" + requestName + "] not found."
            );
        }

        /* 3) Resolve the input class*/
        final Class<INPUT> inputClass = getInputClass(requestName);

        /* 4) Extract to a known input */
        try {
            return httpRequestToObject.map(httpRequest, inputClass);
        } catch (Throwable t) {
            throw Throwables.propagate(
                    new RuntimeException(String.format("Failed to extract input : %s", requestName), t)
            );
        }
    }

    protected String getInputTypeName() {
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) getClass().getGenericSuperclass();
        final Class inputClass = (Class)parameterizedType.getActualTypeArguments()[0];
        return inputClass.getSimpleName();
    }

    protected Response.Status getStatusFrom(final RESPONSE response) {
        final Response.Status status;

        if ( ! response.isOK()) {
            if (null == response.getReason()) {
                status = Response.Status.INTERNAL_SERVER_ERROR;
            } else {
                status = Response.Status.fromStatusCode(CoreReasonHttpCodes.toStatus(response.getReason().getCode()));
            }
        } else {
            status = Response.Status.OK;
        }

        return status;
    }

    protected void sendResponse(
            final HttpServletResponse httpResponse,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final RESPONSE response,
            final UUID kasperCorrelationUUID
    ) throws IOException {
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8");
        httpResponse.addHeader("kasperCorrelationId", kasperCorrelationUUID.toString());
        httpResponse.addHeader(HttpContextHeaders.HEADER_SERVER_NAME, serverName());

        objectToHttpResponse.map(httpResponse, response, getStatusFrom(response));

        flushBuffer(httpResponse);
    }

    protected void sendError(
            final HttpServletResponse httpResponse,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final RESPONSE response,
            final UUID kasperCorrelationUUID,
            final Throwable throwable,
            final Optional<INPUT> input
    ) throws IOException {
        try {
            sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);
        } finally {
            getMetricRegistry().meter(metricNames.getErrorsName()).mark();

            final String inputName = input.isPresent() ? input.get().getClass().getSimpleName() : "undefined";

            if (response.getStatus() == KasperResponse.Status.REFUSED) {
                requestLogger.warn("Refused {} [{}] : {}", getInputTypeName(), inputName, response.getReason(), throwable);
            } else {
                requestLogger.error("Error in {} [{}] : {}", getInputTypeName(), inputName, response.getReason(), throwable);
            }
        }
    }

    // ------------------------------------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void putKey(final String key, final Class newValue, final Map mapping) {
		final Class value = (Class) mapping.get(key);

		if (null != value) {
			throw new HttpExposerError("Duplicate entry for name="
					+ key + ", existing value is " + value.getName());
        }

		mapping.put(key, newValue);
	}

    // ------------------------------------------------------------------------

	protected final String resourceName(final String uri) {
		checkNotNull(uri);

        final String resName;

		final int idx = uri.lastIndexOf('/');
		if (-1 < idx) {
			resName = uri.substring(idx + 1);
		} else {
			resName = uri;
		}

        return Introspector.decapitalize(resName);
	}

    // ------------------------------------------------------------------------

    protected String serverName(){
        if(serverName.isPresent()){
            return serverName.get();
        }

        String fqdn;
        try {
            fqdn = InetAddress.getLocalHost().getCanonicalHostName();
            serverName = Optional.of(fqdn);
        } catch (UnknownHostException e) {
            fqdn = "unknown";
        }
        return fqdn;
    }

    // ------------------------------------------------------------------------

    public AliasRegistry getAliasRegistry() {
        return aliasRegistry;
    }

    // ------------------------------------------------------------------------

    public static class HttpExposerException extends Exception {

        private static final long serialVersionUID = -4342775377554279973L;

        private final CoreReasonCode coreReasonCode;
        private final String message;

        public HttpExposerException(final CoreReasonCode coreReasonCode, final String message){
            this.coreReasonCode = coreReasonCode;
            this.message = message;
        }

        public CoreReasonCode getCoreReasonCode() {
            return coreReasonCode;
        }

        public String getMessage() {
            return message;
        }
    }

    // ------------------------------------------------------------------------

    public static class MetricNames {

        private final String errorsName;
        private final String requestsName;
        private final String requestsTimeName;
        private final String requestsHandleTimeName;

        public MetricNames(final Class clazz) {
            this.errorsName =  name(clazz, "errors");
            this.requestsName = name(clazz, "requests");
            this.requestsTimeName = name(clazz, "requests-time");
            this.requestsHandleTimeName = name(clazz, "requests-handle-time");
        }

        public String getErrorsName() {
            return errorsName;
        }

        public String getRequestsName() {
            return requestsName;
        }

        public String getRequestsTimeName() {
            return requestsTimeName;
        }

        public String getRequestsHandleTimeName() {
            return requestsHandleTimeName;
        }
    }

}
