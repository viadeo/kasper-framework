// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.Domain;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandResolver {

    private static ConcurrentMap<Class, Class> cacheDomains = Maps.newConcurrentMap();

    private DomainLocator domainLocator;
    private CommandHandlerResolver commandHandlerResolver;

    // ------------------------------------------------------------------------

    public String getTypeName() {
        return "Command";
    }

    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(final Class<?> clazz) {

        if ( ! Command.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Optional<CommandHandler<? extends Command>> handler = domainLocator.getHandlerForCommandClass((Class<? extends Command>) clazz);

        if (handler.isPresent()) {
            final Optional<Class<? extends Domain>> domain = commandHandlerResolver.getDomain(handler.getClass());
            if (domain.isPresent()) {
                cacheDomains.put(clazz, domain.get());
                return domain;
            }
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setDomainLocator(final DomainLocator domainLocator) {
        this.domainLocator = checkNotNull(domainLocator);
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

}
