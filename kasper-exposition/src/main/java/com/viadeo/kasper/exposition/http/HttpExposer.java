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
import com.google.common.collect.Lists;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.annotation.XKasperUnexposed;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.security.annotation.XKasperPublic;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_REQUEST_CORRELATION_ID;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public abstract class HttpExposer<INPUT, RESPONSE extends KasperResponse> extends HttpServlet {

    private static final long serialVersionUID = 8448984922303895424L;

    protected static final Logger LOGGER = LoggerFactory.getLogger(HttpExposer.class);

    private final Map<String, Class<INPUT>> exposedInputs;
    private final Map<String, Class<INPUT>> unexposedInputs;

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
        this.exposedInputs = new HashMap<>();
        this.unexposedInputs = new HashMap<>();
    }

    // ------------------------------------------------------------------------

    protected abstract RESPONSE createErrorResponse(final CoreReasonCode code, final List<String> reasons);

    protected abstract RESPONSE createRefusedResponse(final CoreReasonCode code, final List<String> reasons);

    public abstract RESPONSE doHandle(final INPUT input, final Context context) throws Exception;

    protected abstract String toPath(final Class<? extends INPUT> exposedInput);

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

        INPUT input = null;
        RESPONSE response = null;
        ErrorHandlingDescriptor errorHandlingDescriptor = null;

        final Timer.Context timer = getMetricRegistry().timer(metricNames.getRequestsTimeName()).time();
        final UUID kasperCorrelationUUID = UUID.randomUUID();

        try {
            MDC.clear();
            MDC.put(Context.REQUEST_CID_SHORTNAME, extractRequestCorrelationId(httpRequest));
            requestLogger.debug("Processing request in {} [{}]", getInputTypeName(), resourceName(httpRequest.getRequestURI()));

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
            errorHandlingDescriptor = new ErrorHandlingDescriptor(
                    ErrorState.ERROR,
                    exposerException.getCoreReasonCode(),
                    Lists.newArrayList(exposerException.getMessage()),
                    exposerException
            );

        } catch (final JSR303ViolationException validationException) {
            final List<String> errorMessages = new ArrayList<>();
            for (final ConstraintViolation<Object> violation : validationException.getViolations()) {
                errorMessages.add(violation.getPropertyPath() + " : " + violation.getMessage());
            }

            errorHandlingDescriptor = new ErrorHandlingDescriptor(
                    ErrorState.REFUSED,
                    CoreReasonCode.INVALID_INPUT,
                    errorMessages,
                    validationException
            );

        } catch (final IOException e) {
            errorHandlingDescriptor = new ErrorHandlingDescriptor(
                    ErrorState.ERROR,
                    CoreReasonCode.INVALID_INPUT,
                    Lists.newArrayList((null == e.getMessage()) ? "Unknown" : e.getMessage()),
                    e
            );

        } catch (final Throwable th) {
            errorHandlingDescriptor = new ErrorHandlingDescriptor(
                    ErrorState.ERROR,
                    CoreReasonCode.UNKNOWN_REASON,
                    Lists.newArrayList((null == th.getMessage()) ? "Unknown" : th.getMessage()),
                    th
            );
        } finally {
            long duration = timer.stop();
            MDC.put("duration", String.valueOf(TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS)));
        }

        if (null == response && null == errorHandlingDescriptor) {
            errorHandlingDescriptor = new ErrorHandlingDescriptor(
                    ErrorState.ERROR,
                    CoreReasonCode.INTERNAL_COMPONENT_ERROR,
                    Lists.newArrayList("Failed to retrieved a response"),
                    new IllegalStateException()
            );
        }

        try {
            if (null != response) {
                try {

                    /* 5) Respond to the request */
                    sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);

                } catch (final JsonGenerationException | JsonMappingException e) {
                    errorHandlingDescriptor = new ErrorHandlingDescriptor(
                            ErrorState.ERROR,
                            CoreReasonCode.UNKNOWN_REASON,
                            Lists.newArrayList(
                                    String.format(
                                            "Error outputting response to JSON for command [%s] and response [%s]error = %s",
                                            input.getClass().getSimpleName(),
                                            response,
                                            e
                                    )
                            ),
                            e
                    );
                }
            }

            final String inputName = aliasRegistry.resolve(resourceName(httpRequest.getRequestURI()));

            /* 5bis) Manage and respond an error to the request */
            if (null != errorHandlingDescriptor) {
                if (errorHandlingDescriptor.getState() == ErrorState.REFUSED) {
                    response = createRefusedResponse(
                            errorHandlingDescriptor.getCode(),
                            errorHandlingDescriptor.getMessages()
                    );
                } else {
                    response = createErrorResponse(
                            errorHandlingDescriptor.getCode(),
                            errorHandlingDescriptor.getMessages()
                    );
                }

                sendError(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);

                if (errorHandlingDescriptor.getState() == ErrorState.REFUSED) {
                    requestLogger.warn("Refused {} [{}] : <reason={}> <input={}>",
                            getInputTypeName(), inputName, response.getReason(), input,
                            errorHandlingDescriptor.getThrowable()
                    );
                } else {
                    requestLogger.error("Error in {} [{}] : <reason={}> <input={}>",
                            getInputTypeName(), inputName, response.getReason(), input,
                            errorHandlingDescriptor.getThrowable()
                    );
                }

            } else {
                requestLogger.debug("Request processed in {} [{}]", getInputTypeName(), inputName);
            }
        } finally {
            MDC.clear();
        }
    }

    public final RESPONSE handle(final INPUT input, final Context context) throws Exception {
        final Timer.Context globalInputHandleTime = getMetricRegistry().timer(metricNames.getRequestsHandleTimeName()).time();

        try {
            return doHandle(input, context);
        } finally {
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

    protected String extractRequestCorrelationId(final HttpServletRequest httpRequest) {
        String requestCorrelationId = httpRequest.getHeader(HEADER_REQUEST_CORRELATION_ID);
        if (requestCorrelationId == null) {
            requestCorrelationId = UUID.randomUUID().toString();
        }
        return requestCorrelationId;
    }

    protected Context extractContext(
            final HttpServletRequest httpRequest,
            final UUID kasperCorrelationUUID
    ) throws IOException {
        long startMillis = System.currentTimeMillis();

        try {

            final Context context = contextDeserializer.deserialize(httpRequest, kasperCorrelationUUID);

            MDC.setContextMap(context.asMap());

            enrichContextAndMDC(context, "appServer", serverName());
            enrichContextAndMDC(context, "appVersion", meta.getVersion());
            enrichContextAndMDC(context, "appBuildingDate", meta.getBuildingDate().toString());
            enrichContextAndMDC(context, "appDeploymentDate", meta.getDeploymentDate().toString());
            enrichContextAndMDC(context, "clientVersion", Objects.firstNonNull(httpRequest.getHeader(HttpContextHeaders.HEADER_CLIENT_VERSION), "undefined"));
            enrichContextAndMDC(context, "clientId", Objects.firstNonNull(context.getApplicationId(), "undefined"));

            return context;

        } finally {
            long durationMillis = System.currentTimeMillis() - startMillis;
            MDC.put("durationExtractContext", String.valueOf(durationMillis));
        }
    }

    private void enrichContextAndMDC(final Context context, final String key, final String value) {
        MDC.put(key, value);
        if(context != null) {
            context.setProperty(key, value);
        }
    }

    protected INPUT extractInput(
            final HttpServletRequest httpRequest,
            final HttpServletRequestToObject httpRequestToObject
    ) throws HttpExposerException, IOException {
        long startMillis = System.currentTimeMillis();

        try {

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
                throw new RuntimeException(String.format("Failed to extract input : %s (reason: %s)", requestName, t.getMessage()), t);
            }

        } finally {
            long durationMillis = System.currentTimeMillis() - startMillis;
            MDC.put("durationExtractInput", String.valueOf(durationMillis));
        }
    }

    protected String getInputTypeName() {
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) getClass().getGenericSuperclass();
        final Class inputClass = (Class)parameterizedType.getActualTypeArguments()[0];
        return inputClass.getSimpleName();
    }

    protected Response.Status getStatusFrom(final RESPONSE response) {
        final Response.Status status;

        switch (response.getStatus()) {
            case OK:
                status = Response.Status.OK;
                break;
            case ACCEPTED:
                status = Response.Status.ACCEPTED;
                break;
            default:
                if (null == response.getReason()) {
                    status = Response.Status.INTERNAL_SERVER_ERROR;
                } else {
                    status = Response.Status.fromStatusCode(CoreReasonHttpCodes.toStatus(response.getReason().getCode()));
                }
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
            final UUID kasperCorrelationUUID
    ) throws IOException {
        try {
            sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);
        } finally {
            getMetricRegistry().meter(metricNames.getErrorsName()).mark();
        }
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

    public <HANDLER> HttpExposer<INPUT, RESPONSE> expose(final ExposureDescriptor<INPUT,HANDLER> descriptor) {
        checkNotNull(descriptor);

        final String isPublicResource = descriptor.getHandler().getAnnotation(XKasperPublic.class) != null ? "public" : "protected";
        final List<String> aliases = AliasRegistry.aliasesFrom(descriptor.getInput());
        @SuppressWarnings("unchecked")
        final Class<INPUT> inputClass = (Class<INPUT>) descriptor.getInput();
        final String path = toPath(inputClass);
        final String name = inputClass.getSimpleName();

        if ( ! isExposable(descriptor)) {
            LOGGER.info("-> Unexposed {}[{}]", getInputTypeName(), name);
            unexposedInputs.put(path, inputClass);
            return this;
        }

        LOGGER.info("-> Exposing {} {}[{}] at path[/{}]",
                isPublicResource,
                getInputTypeName(),
                name,
                getServletContext().getContextPath() + path);

        for (final String alias : aliases) {
            LOGGER.info("-> Exposing {} {}[{}] at path[/{}]",
                    isPublicResource,
                    getInputTypeName(),
                    name,
                    getServletContext().getContextPath() + alias);
        }

        checkAvailabilityOfResourcePath(path);

        exposedInputs.put(path, inputClass);

        getAliasRegistry().register(path, aliases);

        return this;
    }

    public <HANDLER> boolean isExposable(final ExposureDescriptor<INPUT,HANDLER> descriptor) {
        return ! descriptor.getHandler().isAnnotationPresent(XKasperUnexposed.class);
    }

    protected void checkAvailabilityOfResourcePath(final String path) {
        final Class exposedInput = (Class) exposedInputs.get(path);
        if (null != exposedInput) {
            throw new HttpExposerError(
                    String.format("The resource path is already used by an another input, <path=%s> <input=%s>", path, exposedInput.getName())
            );
        }
    }

    protected boolean isManageable(final String requestName) {
        return exposedInputs.containsKey(checkNotNull(requestName));
    }

    @SuppressWarnings("unchecked")
    protected Class<INPUT> getInputClass(final String inputName) {
        return exposedInputs.get(checkNotNull(inputName));
    }

    protected Map<String, Class<INPUT>> getExposedInputs() {
        return exposedInputs;
    }

    protected Map<String, Class<INPUT>> getUnexposedInputs() {
        return unexposedInputs;
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
        private final String requestsTimeName;
        private final String requestsHandleTimeName;

        public MetricNames(final Class clazz) {
            this.errorsName =  name(clazz, "errors");
            this.requestsTimeName = name(clazz, "requests-time");
            this.requestsHandleTimeName = name(clazz, "requests-handle-time");
        }

        public String getErrorsName() {
            return errorsName;
        }

        public String getRequestsTimeName() {
            return requestsTimeName;
        }

        public String getRequestsHandleTimeName() {
            return requestsHandleTimeName;
        }
    }

    // ------------------------------------------------------------------------

    private static enum ErrorState {
        ERROR, REFUSED
    }

    // ------------------------------------------------------------------------

    private static class ErrorHandlingDescriptor {

        private final ErrorState state;
        private final CoreReasonCode code;
        private final List<String> messages;
        private final Throwable throwable;

        public ErrorHandlingDescriptor(ErrorState state, CoreReasonCode code, List<String> messages, Throwable throwable) {
            this.state = state;
            this.code = code;
            this.messages = messages;
            this.throwable = throwable;
        }

        public ErrorState getState() {
            return state;
        }

        public CoreReasonCode getCode() {
            return code;
        }

        public List<String> getMessages() {
            return messages;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
