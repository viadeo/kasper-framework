// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposer extends HttpExposer {
    private static final long serialVersionUID = 8444284922303895624L;
    protected static final transient Logger REQUEST_LOGGER = LoggerFactory.getLogger(HttpCommandExposer.class);

    private final Map<String, Class<? extends Command>> exposedCommands = new HashMap<>();
    private final transient DomainLocator domainLocator;
    private final ObjectMapper mapper;
    private final transient CommandGateway commandGateway;

    // ------------------------------------------------------------------------

    public HttpCommandExposer(final CommandGateway commandGateway, final DomainLocator domainLocator) {
        this(commandGateway, domainLocator, ObjectMapperProvider.INSTANCE.mapper());
    }
    
    public HttpCommandExposer(final CommandGateway commandGateway, final DomainLocator domainLocator, final ObjectMapper mapper) {
        this.commandGateway = commandGateway;
        this.domainLocator = checkNotNull(domainLocator);
        this.mapper = mapper;
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing commands ===============");

        for (final CommandHandler<? extends Command> handler : domainLocator.getHandlers()) {
            expose(handler);
        }

        if (exposedCommands.isEmpty()) {
            LOGGER.warn("No Command has been exposed.");
        } else {
            LOGGER.info("Total exposed " + exposedCommands.size() + " commands.");
        }

        LOGGER.info("=================================================");
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
        resp.flushBuffer();
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleCommand(req, resp);

        /*
         * must be last call to ensure that everything is sent to the client
         *(even if an error occurred)
         */
        resp.flushBuffer();
    }

    // ------------------------------------------------------------------------

    private void handleCommand(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        String uuid = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        MDC.put("correlationId", uuid);
        resp.addHeader("UUID",uuid);
        REQUEST_LOGGER.info("Processing Command : " + req.getMethod() + " " + getFullRequestURI(req));

        /* always respond with a json stream (even if empty) */
        resp.setContentType("application/json; charset=utf-8");

        // FIXME can throw an error ensure to respond a json stream
        final String commandName = resourceName(req.getRequestURI());

        /* locate corresponding command class */
        final Class<? extends Command> commandClass = exposedCommands.get(commandName);
        if (null == commandClass) {
            REQUEST_LOGGER.info("Response: '{}' Execution Time '{}' ms ",HttpServletResponse.SC_NOT_FOUND, System.currentTimeMillis() - start );
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Command[" + commandName + "] not found.");
            return;
        }

        CommandResult result = null;
        JsonParser parser = null;

        try {

            if (!req.getContentType().contains("application/json")) {
                REQUEST_LOGGER.info("Response: '{}' Execution Time '{}' ms ",HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, System.currentTimeMillis() - start );
                sendError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                          "Accepting and producing only application/json");
                return;
            }

            final ObjectReader reader = mapper.reader();

            /* parse the input stream to that command, no utility method for inputstream+type?? */
            parser = reader.getFactory().createJsonParser(req.getInputStream());
            final Command command = reader.readValue(parser, commandClass);

            // FIXME 1 use context from request
            // FIXME 2 does it make sense to have async commands here? In any
            // case the user is expecting a result success or failure

            /* send now that command to the platform and wait for the result */
            result = commandGateway.sendCommandAndWaitForAResult(
                    command, new DefaultContextBuilder().build());

        } catch (final IOException e) {
            LOGGER.error("Error parse command [" + commandClass.getName() + "]", e);
            final String errorMessage = (null == e.getMessage()) ? "Unknown" : e.getMessage();
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, errorMessage);
        } catch (final Throwable th) {
            // we catch any other exception in order to still respond with json
            LOGGER.error("Error for command [" + commandClass.getName() + "]", th);
            final String errorMessage = (null == th.getMessage()) ? "Unknown" : th.getMessage();
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
        } finally {
            if (null != parser) {
                /*
                 * FIXME check if jackson is closing the underlying inputstream,
                 * we still must close it inorder to allow Jackson to recycle
                 * its buffers
                 */
                parser.close();
            }
        }

        /*
         * if the result is null this means that we handled the error previously
         * so nothing can be done anymore
         */
        if (null != result) {
            sendResponse(result, resp, commandClass);
            REQUEST_LOGGER.info("Response: '"+resp.getStatus()+"' Execution Time '"+(System.currentTimeMillis() - start)+"' ms ");
        }
    }

    // ------------------------------------------------------------------------

    protected void sendResponse(final CommandResult result, final HttpServletResponse resp,
                                final Class<? extends Command> commandClass) throws IOException {

        final ObjectWriter writer = mapper.writer();
        JsonGenerator generator = null;

        try {

            /* try writing the response */
            generator = writer.getJsonFactory().createJsonGenerator(resp.getOutputStream());

            if (result.isError()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }

            writer.writeValue(generator, result);

        } catch (final JsonGenerationException e) {
            this.internalCommandError(resp, commandClass, result, e);
        } catch (final JsonMappingException e) {
            this.internalCommandError(resp, commandClass, result, e);
        } catch (final IOException e) {
            this.internalCommandError(resp, commandClass, result, e);
        } finally {
            if (generator != null) {
                generator.flush();
                generator.close();
            }
        }
    }

    private void internalCommandError(final HttpServletResponse resp, final Class<? extends Command> commandClass,
                                      final CommandResult result, final Exception e) throws IOException {
         this.sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                  String.format("Error outputting result to JSON for command [%s] and result [%s]error = %s",
                          commandClass.getSimpleName(), result, e));
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
    protected void sendError(final HttpServletResponse response, final int status, final String reason)
            throws IOException {
        LOGGER.error(reason);

        /* set an error status and a message */
        response.setStatus(status, checkNotNull(reason));

        /* write also into the body the result as json */
        mapper.writer().writeValue(response.getOutputStream(),
                                   CommandResult.error(
                                          new KasperError(CoreErrorCode.UNKNOWN_ERROR, reason)));
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpExposer expose(final CommandHandler<? extends Command> commandHandler) {
        checkNotNull(commandHandler);

        final TypeToken<? extends CommandHandler> typeToken = TypeToken.of(commandHandler.getClass());

        final Class<? super Command> commandClass = (Class<? super Command>) typeToken
                .getSupertype(CommandHandler.class)
                .resolveType(CommandHandler.class.getTypeParameters()[0])
                .getRawType();

        final String commandPath = commandToPath(commandClass);

        LOGGER.info("-> Exposing command[{}] at path[/{}]",
                    commandClass.getSimpleName(),
                    getServletContext().getContextPath() + commandPath);

        putKey(commandPath, commandClass, exposedCommands);

        return this;
    }

    // ------------------------------------------------------------------------

    private String commandToPath(final Class<? super Command> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
