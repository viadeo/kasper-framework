// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class HttpCommandExposer extends HttpExposer {

    private static final long serialVersionUID = 8444284922303895624L;

    protected static final transient Logger REQUEST_LOGGER = LoggerFactory.getLogger(HttpCommandExposer.class);

    private static final String GLOBAL_TIMER_REQUESTS_TIME_NAME = name(HttpCommandExposer.class, "requests-time");
    private static final String GLOBAL_TIMER_REQUESTS_HANDLE_TIME_NAME = name(HttpCommandExposer.class, "requests-handle-time");
    private static final String GLOBAL_METER_REQUESTS_NAME = name(HttpCommandExposer.class, "requests");
    private static final String GLOBAL_METER_ERRORS_NAME = name(HttpCommandExposer.class, "errors");

    private final Map<String, Class<? extends Command>> exposedCommands = new HashMap<>();
    private final transient List<ExposureDescriptor<Command,CommandHandler>> descriptors;
    private final ObjectMapper mapper;
    private final transient CommandGateway commandGateway;
    private final transient HttpContextDeserializer contextDeserializer;
    private final AliasRegistry aliasRegistry;

    // ------------------------------------------------------------------------

    public HttpCommandExposer(final CommandGateway commandGateway,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors) {
        this(
                commandGateway,
                descriptors,
                new HttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }
    
    public HttpCommandExposer(final CommandGateway commandGateway,
                              final List<ExposureDescriptor<Command,CommandHandler>> descriptors,
                              final HttpContextDeserializer contextDeserializer,
                              final ObjectMapper mapper) {
        this.commandGateway = checkNotNull(commandGateway);
        this.descriptors = checkNotNull(descriptors);
        this.contextDeserializer = checkNotNull(contextDeserializer);
        this.mapper = checkNotNull(mapper);
        this.aliasRegistry = new AliasRegistry();
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing commands ===============");

        for (final ExposureDescriptor<Command,CommandHandler> descriptor : descriptors) {
            expose(descriptor);
        }

        if (exposedCommands.isEmpty()) {
            LOGGER.warn("No Command has been exposed.");
        } else {
            LOGGER.info("Total exposed " + exposedCommands.size() + " commands.");
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleCommand(req, resp);

        /*
         * must be last call to ensure that everything is sent to the client
         * (even if an error occurred)
         */
        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleCommand(req, resp);

        /*
         * must be last call to ensure that everything is sent to the client
         *(even if an error occurred)
         */
        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }
    }

    // ------------------------------------------------------------------------

    private void handleCommand(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        /* Start request timer */
        final Timer.Context classTimer = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_TIME_NAME).time();

        /* Create a request correlation id */
        final UUID kasperCorrelationUUID = UUID.randomUUID();
        resp.addHeader("kasperCorrelationId", kasperCorrelationUUID.toString());

        /* extract context from request */
        final Context context = contextDeserializer.deserialize(req,kasperCorrelationUUID);
        MDC.setContextMap(context.asMap());

        /* Log starting request */
        REQUEST_LOGGER.info("Processing HTTP Command '{}' '{}'", req.getMethod(), getFullRequestURI(req));

        /* always respond with a json stream (even if empty) */
        resp.setContentType(MediaType.APPLICATION_JSON + "; charset=utf-8");

        // FIXME can throw an error ensure to respond a json stream
        final String commandName = aliasRegistry.resolve(resourceName(req.getRequestURI()));

        /* locate corresponding command class */
        final Class<? extends Command> commandClass = exposedCommands.get(commandName);
        if (null == commandClass) {
            sendError(req, resp, Response.Status.NOT_FOUND.getStatusCode(),
                      "Command[" + commandName + "] not found.");
            return;
        }

        CommandResponse response = null;
        JsonParser parser = null;

        try {

            if ((null == req.getContentType()) || ( ! req.getContentType().contains(MediaType.APPLICATION_JSON_VALUE))) {
                sendError(req, resp, Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
                          "Accepting and producing only " + MediaType.APPLICATION_JSON_VALUE);
                return;
            }

            final ObjectReader reader = mapper.reader();

            /* parse the input stream to that command, no utility method for input stream+type?? */
            parser = reader.getFactory().createJsonParser(req.getInputStream());
            final Command command = reader.readValue(parser, commandClass);

            /* send now that command to the platform and wait for the result */
            final Timer.Context commandHandleTime = getMetricRegistry().timer(name(command.getClass(), "requests-handle-time")).time();
            final Timer.Context classHandleTime = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_HANDLE_TIME_NAME).time();

            response = commandGateway.sendCommandAndWaitForAResponseWithException(command, context);
            checkNotNull(response);

            if (response.isOK() && response.getSecurityToken().isPresent()) {
                resp.addHeader(HttpContextHeaders.HEADER_SECURITY_TOKEN, response.getSecurityToken().get());
            }

            commandHandleTime.stop();
            classHandleTime.stop();

        } catch (final JSR303ViolationException validationException) {

            final List<String> errorMessages = new ArrayList<>();
            for (final ConstraintViolation<Object> violation : validationException.getViolations()) {
                errorMessages.add(violation.getPropertyPath() + " : " + violation.getMessage());
            }

            sendResponse(
                    CommandResponse.error(
                        new KasperReason(
                            CoreReasonCode.INVALID_INPUT.name(),
                            errorMessages
                        )
                    ),
                    req, resp, commandClass);

        } catch (final IOException e) {

            LOGGER.error("Error in command [" + commandClass.getName() + "]", e);
            final String errorMessage = (null == e.getMessage()) ? "Unknown" : e.getMessage();
            sendError(req, resp, Response.Status.BAD_REQUEST.getStatusCode(), errorMessage);

        } catch (final Throwable th) {
            // we catch any other exception in order to still respond with json
            LOGGER.error("Error in command [" + commandClass.getName() + "]", th);
            final String errorMessage = (null == th.getMessage()) ? "Unknown" : th.getMessage();
            sendError(req, resp, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorMessage);

        } finally {
            if (null != parser) {
                /*
                 * FIXME check if jackson is closing the underlying inputstream,
                 * we still must close it in order to allow Jackson to recycle
                 * its buffers
                 */
                parser.close();
            }

            /* Log & metrics */
            final long time = classTimer.stop();
            REQUEST_LOGGER.info("Execution Time '{}' ns",time);
            getMetricRegistry().meter(GLOBAL_METER_REQUESTS_NAME).mark();
        }

        if (null != response) {
            sendResponse(response, req, resp, commandClass);
        }
    }

    // ------------------------------------------------------------------------

    protected void sendResponse(final CommandResponse response,
                                final HttpServletRequest req, final HttpServletResponse resp,
                                final Class<? extends Command> commandClass)
            throws IOException {

        final ObjectWriter writer = mapper.writer();
        JsonGenerator generator = null;

        final int status;
        if ( ! response.isOK()) {
            if (null == response.getReason()) {
                status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            } else {
                status = CoreReasonHttpCodes.toStatus(response.getReason().getCode());
            }
        } else {
            status = Response.Status.OK.getStatusCode();
        }

        try {

            /* try writing the response */
            generator = writer.getJsonFactory().createJsonGenerator(resp.getOutputStream());
            resp.setStatus(status);
            writer.writeValue(generator, response);

        } catch (final JsonGenerationException | JsonMappingException e) {

            this.internalCommandError(req, resp, commandClass, response, e);

        } finally {

            if (generator != null) {
                generator.flush();
                generator.close();
            }

            /* Log request */
            REQUEST_LOGGER.info("HTTP Response {} '{}' : {}", req.getMethod(), req.getRequestURI(), status);
        }
    }

    private void internalCommandError(final HttpServletRequest req, final HttpServletResponse resp,
                                      final Class<? extends Command> commandClass, final CommandResponse response,
                                      final Exception e)
            throws IOException {
         this.sendError(req, resp, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                  String.format("Error outputting response to JSON for command [%s] and response [%s]error = %s",
                          commandClass.getSimpleName(), response, e)
         );
    }

    // ------------------------------------------------------------------------

    /**
     * Will try to send an error by setting the right http status and writing a json response in the body. If it fails
     * there will be no json body.
     *
     * we need to use setStatus because sendError commits the response,
     * preventing us from writing the respone as json it also forces response to
     * text/html.
     */
    @SuppressWarnings("deprecation")
    protected void sendError(final HttpServletRequest request, final HttpServletResponse response, final int status, final String reason)
            throws IOException {
        LOGGER.error(reason);

        /* set an error status and a message */
        response.setStatus(status, checkNotNull(reason));

        /* write also into the body the response as json */
        mapper.writer().writeValue(response.getOutputStream(),
                                   CommandResponse.error(
                                          new KasperReason(CoreReasonCode.UNKNOWN_REASON, reason)
                                   )
        );

        /* Log request */
        REQUEST_LOGGER.info("HTTP Response {} '{}' : {} {}", request.getMethod(), request.getRequestURI(), status, reason);

        /* Log error metric */
        getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpExposer expose(final ExposureDescriptor<Command,CommandHandler> descriptor) {
        checkNotNull(descriptor);

        final TypeToken<? extends CommandHandler> typeToken = TypeToken.of(descriptor.getHandler());

        final Class<? super Command> commandClass = (Class<? super Command>) typeToken
                .getSupertype(CommandHandler.class)
                .resolveType(CommandHandler.class.getTypeParameters()[0])
                .getRawType();

        final String commandPath = commandToPath(commandClass);
        final List<String> aliases = AliasRegistry.aliasesFrom(descriptor.getHandler());
        final String commandName = commandClass.getSimpleName();

        LOGGER.info("-> Exposing command[{}] at path[/{}]",
                commandName,
                    getServletContext().getContextPath() + commandPath);

        for (final String alias : aliases) {
            LOGGER.info("-> Exposing command[{}] at path[/{}]",
                    commandName,
                    getServletContext().getContextPath() + alias);
        }

        putKey(commandPath, commandClass, exposedCommands);

        aliasRegistry.register(commandPath, aliases);

        return this;
    }

    // ------------------------------------------------------------------------

    private String commandToPath(final Class<? super Command> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
