// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandHandlerResolver extends AbstractResolver<CommandHandler> {

    private static ConcurrentMap<Class, Class> cacheCommands = Maps.newConcurrentMap();

    private CommandResolver commandResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "CommandHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends CommandHandler> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperCommandHandler handlerAnnotation = clazz.getAnnotation(XKasperCommandHandler.class);
        if (null != handlerAnnotation) {
            final Class<? extends Domain> domain = handlerAnnotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        } else {
            throw new KasperException("Command handler is not decorated : " + clazz.getName());
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends CommandHandler> handlerClazz) {
        final XKasperCommandHandler annotation = handlerClazz.getAnnotation(XKasperCommandHandler.class);

        String description = "";
        if (null != annotation) {
            description = annotation.description();
        }
        if (description.isEmpty()) {
            description = String.format("The %s command handler", this.getLabel(handlerClazz));
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends CommandHandler> clazz) {
        return clazz.getSimpleName()
                .replace("CommandHandler", "")
                .replace("Handler", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Command> getCommandClass(final Class<? extends CommandHandler> clazz) {

        if (cacheCommands.containsKey(clazz)) {
            return cacheCommands.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Command>> commandClazz =
                (Optional<Class<? extends Command>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION);

        if (!commandClazz.isPresent()) {
            throw new KasperException("Unable to find command type for handler " + clazz.getClass());
        }

        cacheCommands.put(clazz, commandClazz.get());

        return commandClazz.get();
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        super.clearCache();
        cacheCommands.clear();
    }

    // ------------------------------------------------------------------------

    public void setCommandResolver(final CommandResolver commandResolver) {
        this.commandResolver = checkNotNull(commandResolver);
    }

}
