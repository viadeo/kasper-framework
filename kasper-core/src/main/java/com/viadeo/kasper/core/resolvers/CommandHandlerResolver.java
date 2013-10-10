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

public class CommandHandlerResolver {

    private static ConcurrentMap<Class, Class> cacheDomains = Maps.newConcurrentMap();
    private static ConcurrentMap<Class, Class> cacheCommands = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    public String getTypeName() {
        return "CommandHandler";
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(final Class<?> clazz) {

        if ( ! CommandHandler.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperCommandHandler handlerAnnotation = clazz.getAnnotation(XKasperCommandHandler.class);
        if (null != handlerAnnotation) {
            final Class<? extends Domain> domain = handlerAnnotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Command>> getCommandClass(final Class<CommandHandler<? extends CommandHandler>> clazz) {

        if (cacheCommands.containsKey(clazz)) {
            return Optional.<Class<? extends Command>>of(cacheCommands.get(clazz));
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

        return commandClazz;
    }

}
