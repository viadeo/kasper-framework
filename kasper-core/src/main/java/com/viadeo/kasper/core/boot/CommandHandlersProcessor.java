// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Process Kasper command handlers dynamic registration at kasper platform boot
 *
 * @see XKasperCommandHandler
 */
public class CommandHandlersProcessor extends SingletonAnnotationProcessor<XKasperCommandHandler, CommandHandler> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlersProcessor.class);

	/**
	 * The command bus to register on
	 */
	private transient CommandBus commandBus;

    /**
     * The event bus to use for events publishing
     */
    private transient EventBus eventBus;

	/**
	 * The domain locator to be passed to command handlers
	 */
	private transient DomainLocator domainLocator;

    /**
     * The command handler resolver
     */
    private transient CommandHandlerResolver commandHandlerResolver;

	// ------------------------------------------------------------------------

	/**
	 *
	 * Convenient class for Axon command bus subscription proper dynamic typing
	 *
	 * @param <C> the Kasper command type handled
	 */
	private static class AxonCommandCastor<C extends Command> {

		private final transient Class<? extends C> answer;
		private final transient org.axonframework.commandhandling.CommandHandler handler;

		@SuppressWarnings("unchecked") // Safe by previous parent class typing
        AxonCommandCastor(final Class bean, final org.axonframework.commandhandling.CommandHandler container) {
			this.answer = (Class<? extends C>) bean;
			this.handler = container;
		}

		public Class<? extends C> getBeanClass() {
			return this.answer;
		}

		public org.axonframework.commandhandling.CommandHandler getContainerClass() {
			return this.handler;
		}
	}

	//-------------------------------------------------------------------------

	/**
	 * Process Kasper command handlers
	 * 
	 * @see com.viadeo.kasper.cqrs.command.CommandHandler
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class commandHandlerClazz, final CommandHandler commandHandler) {
		LOGGER.info("Subscribe to command bus : " + commandHandlerClazz.getName());

		if (AbstractCommandHandler.class.isAssignableFrom(commandHandler.getClass())) {
			((AbstractCommandHandler) commandHandler).setDomainLocator(this.domainLocator);
 			((AbstractCommandHandler) commandHandler).setEventBus(this.eventBus);
		}
		
		//- Retrieve command type from command handler definition -------------
        @SuppressWarnings("unchecked")
        final Class<? extends Command> commandClass =
                commandHandlerResolver.getCommandClass((Class<? extends CommandHandler>) commandHandlerClazz);

        // register this command handler for further use in kasper components
        domainLocator.registerHandler(commandHandler);

        //- Dynamic type command class and command handler for Axon -------
        final AxonCommandCastor<Command> castor =
                new AxonCommandCastor<>(commandClass, commandHandler);

        //- Subscribe the handler to this command type (Axon) -------------
        this.commandBus.subscribe(castor.getBeanClass().getName(), castor.getContainerClass());
	}

	// ------------------------------------------------------------------------

	/**
	 * @param commandBus the command bus to register command handlers on
	 */
	public void setCommandBus(final CommandBus commandBus) {
		this.commandBus = checkNotNull(commandBus);
	}

	/**
	 * @param domainLocator the domain locator to inject on command handlers
	 */
	public void setDomainLocator(final DomainLocator domainLocator) {
		this.domainLocator = checkNotNull(domainLocator);
	}

 	/**
	 * @param eventBus the event bus to register command handlers on
	 */
	public void setEventBus(final EventBus eventBus) {
		this.eventBus = checkNotNull(eventBus);
	}

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

}
