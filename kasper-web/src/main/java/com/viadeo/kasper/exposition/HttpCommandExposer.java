// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Introspector;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposer extends HttpExposer<Command, CommandResponse> {

    private static final long serialVersionUID = 8444284922303895624L;

    private final Map<String, Class<? extends Command>> exposedCommands = new HashMap<>();
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
    protected boolean isManageable(final String inputName) {
        return exposedCommands.containsKey(checkNotNull(inputName));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends Command> getInputClass(final String inputName) {
        return exposedCommands.get(checkNotNull(inputName));
    }

    @Override
    public CommandResponse doHandle(final Command command, final Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponseWithException(command, context);
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
        if (response.isOK() && response.getSecurityToken().isPresent()) {
            httpResponse.addHeader(HttpContextHeaders.HEADER_SECURITY_TOKEN, response.getSecurityToken().get());
        }
        super.sendResponse(httpResponse, objectToHttpResponse, response, kasperCorrelationUUID);
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
        final List<String> aliases = AliasRegistry.aliasesFrom(commandClass);
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

        getAliasRegistry().register(commandPath, aliases);

        return this;
    }

    // ------------------------------------------------------------------------

    private String commandToPath(final Class<? super Command> exposedCommand) {
        return Introspector.decapitalize(exposedCommand.getSimpleName().replaceAll("Command", ""));
    }

}
