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
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.cqrs.command.impl.KasperErrorCommandResult;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.platform.IPlatform;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposer extends HttpExposer {
    private static final long serialVersionUID = 8444284922303895624L;

    private final Map<String, Class<? extends ICommand>> exposedCommands = new HashMap<>();
    private IDomainLocator domainLocator;

    // ------------------------------------------------------------------------

    public HttpCommandExposer(final IPlatform platform, final IDomainLocator domainLocator) {
        super(platform);
        this.domainLocator = checkNotNull(domainLocator);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing commands ===============");
        for (final ICommandHandler<? extends ICommand> handler : domainLocator.getHandlers()) {
            expose(handler);
        }
        if (exposedCommands.isEmpty()) LOGGER.warn("No Command has been exposed.");
        else LOGGER.info("Total exposed " + exposedCommands.size() + " commands.");
        LOGGER.info("=================================================");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {

        handleCommand(req, resp);

        // must be last call to ensure that everything is sent to the client
        // (even if an error occurred)
        resp.flushBuffer();
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {

        handleCommand(req, resp);

        // must be last call to ensure that everything is sent to the client
        // (even if an error occurred)
        resp.flushBuffer();
    }

    // ------------------------------------------------------------------------

    private void handleCommand(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        // always respond with a json stream (even if empty)
        resp.setContentType("application/json; charset=utf-8");

        // FIXME can throw an error ensure to respond a json stream
        String commandName = resourceName(req.getRequestURI());

        // locate corresponding command class
        Class<? extends ICommand> commandClass = exposedCommands.get(commandName);
        if (null == commandClass) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Command[" + commandName + "] not found.");
            return;
        }

        ICommandResult result = null;
        JsonParser parser = null;

        try {

            if (!req.getContentType().startsWith("application/json")) {
                sendError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "Accepting and producing only application/json");
                return;
            }

            final ObjectReader reader = ObjectMapperProvider.instance.objectReader();

            // parse the input stream to that command, no utility method for inputstream+type??
            parser = reader.getFactory().createJsonParser(req.getInputStream());
            ICommand command = reader.readValue(parser, commandClass);

            // FIXME 1 use context from request
            // FIXME 2 does it make sense to have async commands here? In any
            // case the user is expecting a result success or failure

            // send now that command to the platform and wait for the result
            result = platform().getCommandGateway().sendCommandAndWaitForAResult(command,
                    new DefaultContextBuilder().buildDefault());

        } catch (final IOException e) {

            LOGGER.error("Error parse command [" + commandClass.getName() + "]", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The command could not be parsed.");

        } catch (final Throwable th) {

            // we catch any other exception in order to still respond with json
            LOGGER.error("Error for command [" + commandClass.getName() + "]", th);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occured preventing us from handling your command.");

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

        // if the result is null this means that we handled the error previously
        // so nothing can be done anymore
        if (result != null) {
            sendResponse(result, resp, commandClass);
        }
    }

    // ------------------------------------------------------------------------

    protected void sendResponse(final ICommandResult result, final HttpServletResponse resp,
            final Class<? extends ICommand> commandClass) throws IOException {

        final ObjectWriter writer = ObjectMapperProvider.instance.objectWriter();
        JsonGenerator generator = null;

        try {

            // try writing the response
            generator = writer.getJsonFactory().createJsonGenerator(resp.getOutputStream());
            writer.writeValue(generator, result);
            /*
             * FIXME how to handle errors here, jackson started writing so we
             * don't have any guarantees about the content that has been up to
             * now
             */
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (final JsonGenerationException e) {
            LOGGER.error("Error outputing command result to json for command [" + commandClass.getName()
                    + "] and result [" + result + "]", e);
        } catch (final JsonMappingException e) {
            LOGGER.error("Error mapping command result to json for command [" + commandClass.getName()
                    + "] and result [" + result + "]", e);
        } catch (final IOException e) {
            LOGGER.error("Error outputing command result to json for command [" + commandClass.getName()
                    + "] and result [" + result + "]", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occured during the generation of the response.");
        } finally {
            if (generator != null) {
                generator.flush();
                generator.close();
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Will try to send an error by setting the right http status and writing a json response in the body. If it fails
     * there will be no json body.
     */
    /*
     * we need to use setStatus because sendError commits the response,
     * preventing us from writing the respone as json it also forces response to
     * text/html.
     */
    @SuppressWarnings("deprecation")
    protected void sendError(HttpServletResponse response, int status, String reason) throws IOException {
        LOGGER.error(reason);
        // set an error status and a message
        response.setStatus(status, reason);
        // write also into the body the result as json
        ObjectMapperProvider.instance.objectWriter()
                .writeValue(response.getOutputStream(), new KasperErrorCommandResult(reason));
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpExposer expose(final ICommandHandler<? extends ICommand> commandHandler) {
        checkNotNull(commandHandler);
        final TypeToken<? extends ICommandHandler> typeToken = TypeToken.of(commandHandler.getClass());
        final Class<? super ICommand> commandClass = (Class<? super ICommand>) typeToken
                .getSupertype(ICommandHandler.class).resolveType(ICommandHandler.class.getTypeParameters()[0])
                .getRawType();
        final String commandPath = commandToPath(commandClass);
        LOGGER.info("Exposing command[{}] at path[/{}]", commandClass.getSimpleName(), getServletContext().getContextPath()
                + commandPath);
        putKey(commandPath, commandClass, exposedCommands);
        return this;
    }

    // ------------------------------------------------------------------------

    private String commandToPath(final Class<? super ICommand> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
