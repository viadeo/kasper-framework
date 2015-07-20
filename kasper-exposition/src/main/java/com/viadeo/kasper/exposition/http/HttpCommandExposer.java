// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposer extends HttpExposer<Command, CommandResponse> {

    private static final long serialVersionUID = 8444284922303895624L;

    private final transient List<ExposureDescriptor<Command,CommandHandler>> descriptors;
    private final transient CommandGateway commandGateway;

    private final ObjectToHttpServletResponse objectToHttpResponse;
    private final HttpServletRequestToObject httpRequestToObject;

    // ------------------------------------------------------------------------

    public HttpCommandExposer(final Platform platform,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors) {
        this(
                platform.getCommandGateway(),
                platform.getMeta(),
                descriptors,
                new HttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }
    
    public HttpCommandExposer(final CommandGateway commandGateway,
                              final Meta meta,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors,
                              final HttpContextDeserializer contextDeserializer,
                              final ObjectMapper mapper) {
        super(contextDeserializer, meta);
        this.commandGateway = checkNotNull(commandGateway);
        this.descriptors = checkNotNull(descriptors);

        this.httpRequestToObject = new HttpServletRequestToObject.JsonToObjectMapper(mapper);
        this.objectToHttpResponse = new ObjectToHttpServletResponse(mapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing commands ===============");

        for (final ExposureDescriptor<Command,CommandHandler> descriptor : descriptors) {
            expose(descriptor);
        }

        LOGGER.info("Total exposed {} commands.", getExposedInputs().size());

        if ( ! getUnexposedInputs().isEmpty()) {
            LOGGER.info("Total unexposed {} commands.", getUnexposedInputs().size());
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected CommandResponse createErrorResponse(final CoreReasonCode code, final List<String> reasons) {
        return CommandResponse.error(new KasperReason(code, reasons));
    }

    @Override
    protected CommandResponse createRefusedResponse(final CoreReasonCode code, final List<String> reasons) {
        return CommandResponse.refused(new KasperReason(code, reasons));
    }

    @Override
    public CommandResponse doHandle(final Command command, final Context context) throws Exception {
        CallTypes.CallType callType = CallTypes.SYNC;

        final Optional<Serializable> optionalProperty = context.getProperty(Context.CALL_TYPE);

        if (optionalProperty.isPresent()) {
            final String callTypeAsString = String.valueOf(optionalProperty.get());
            final Optional<CallTypes.CallType> optionalCallType = CallTypes.of(callTypeAsString);

            if (optionalCallType.isPresent()) {
                callType = optionalCallType.get();
            }
        }

        return callType.doCall(commandGateway, command, context);
    }

    @Override
    protected void checkMediaType(final HttpServletRequest httpRequest) throws HttpExposerException {
        if ((null == httpRequest.getContentType()) || ( ! httpRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE))) {
            throw new HttpExposerException(
                    CoreReasonCode.UNSUPPORTED_MEDIA_TYPE,
                    "Accepting and producing only " + MediaType.APPLICATION_JSON_VALUE
            );
        }
    }

    @Override
    protected void sendResponse(
            final HttpServletResponse httpResponse,
            final ObjectToHttpServletResponse objectToHttpResponse,
            final CommandResponse response,
            final UUID kasperCorrelationUUID
    ) throws IOException {
        if (response.isOK()) {
            if (response.getSecurityToken().isPresent()) {
                httpResponse.addHeader(HttpContextHeaders.HEADER_SECURITY_TOKEN.toHeaderName(), response.getSecurityToken().get());
            }
            if (response.getAccessToken().isPresent()) {
                httpResponse.addHeader(HttpContextHeaders.HEADER_ACCESS_TOKEN.toHeaderName(), response.getAccessToken().get());
            }
        }
        super.sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);
    }

    // ------------------------------------------------------------------------

    @Override
    protected String toPath(final Class<? extends Command> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
