// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.api.documentation.XKasperCommand;
import com.viadeo.kasper.api.domain.Domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandResolver extends AbstractResolver<Command> {

    private DomainLocator domainLocator;
    private CommandHandlerResolver commandHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Command";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Command> clazz) {

        if ( ! Command.class.isAssignableFrom(checkNotNull(clazz))) {
            return Optional.absent();
        }

        if (DOMAINS_CACHE.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        if (null != domainLocator) {
            final Optional<CommandHandler> handler = domainLocator.getHandlerForCommandClass(clazz);

            if (handler.isPresent()) {
                final Optional<Class<? extends Domain>> domain =
                        commandHandlerResolver.getDomainClass(handler.get().getClass());
                if (domain.isPresent()) {
                    DOMAINS_CACHE.put(clazz, domain.get());
                    return domain;
                }
            }
        } else {
            return domainResolver.getDomainClassOf(clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Command> commandClazz) {
 		final XKasperCommand annotation =
                checkNotNull(commandClazz).getAnnotation(XKasperCommand.class);

		// Get description ----------------------------------------------------
		String description = "";

        if (null != annotation) {
           description = annotation.description();
        }

		if (description.isEmpty()) {
			description = String.format("The %s command", this.getLabel(commandClazz));
		}

        return description;
    }

    public String getLabel(Class<? extends Command> commandClass) {
        return checkNotNull(commandClass).getSimpleName().replace("Command", "");
    }

    // ------------------------------------------------------------------------

    public void setDomainLocator(final DomainLocator domainLocator) {
        this.domainLocator = checkNotNull(domainLocator);
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

}
