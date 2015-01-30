// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CommandHandlerResolver extends AbstractResolver<CommandHandler> {

    private static ConcurrentMap<Class, Class> cacheCommands = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "CommandHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends CommandHandler> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperCommandHandler handlerAnnotation = clazz.getAnnotation(XKasperCommandHandler.class);
        if (null != handlerAnnotation) {
            final Class<? extends Domain> domain = handlerAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends CommandHandler> handlerClazz) {
        final XKasperCommandHandler annotation =
                checkNotNull(handlerClazz).getAnnotation(XKasperCommandHandler.class);

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
        return checkNotNull(clazz).getSimpleName()
                .replace("CommandHandler", "")
                .replace("Handler", "")
                .replace("QueryService", "")
                .replace("Service", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Command> getCommandClass(final Class<? extends CommandHandler> clazz) {

        if (cacheCommands.containsKey(checkNotNull(clazz))) {
            return cacheCommands.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Command>> commandClazz =
                (Optional<Class<? extends Command>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                CommandHandler.class,
                                CommandHandler.COMMAND_PARAMETER_POSITION
                        );

        if ( ! commandClazz.isPresent()) {
            throw new KasperException("Unable to find command type for handler " + clazz.getClass());
        }

        putCommandClass(clazz, commandClazz);

        return commandClazz.get();
    }

    @VisibleForTesting
    protected void putCommandClass(Class<? extends CommandHandler> clazz, Optional<Class<? extends Command>> commandClazz) {
        checkNotNull(clazz);
        checkNotNull(commandClazz);
        checkArgument(commandClazz.isPresent());
        cacheCommands.put(clazz, commandClazz.get());
    }

    @SuppressWarnings("unchecked")
    // @javax.annotation.Nullable
    public Optional<Class<? extends CommandHandler>> getHandlerClass(Class<? extends Command> commandClass) {
        checkNotNull(commandClass);
        for (Map.Entry<Class, Class> handlerToCommand : cacheCommands.entrySet()) {
            if (commandClass.equals(handlerToCommand.getValue())) {
                return Optional.<Class<? extends CommandHandler>>of(handlerToCommand.getKey());
            }
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        super.clearCache();
        cacheCommands.clear();
    }

}
