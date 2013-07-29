// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractCommandHandler;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.commandhandling.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper command handlers dynamic registration at kasper platform boot
 *
 * @see XKasperCommandHandler
 */
public class CommandHandlersProcessor extends SingletonAnnotationProcessor<XKasperCommandHandler, CommandHandler<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlersProcessor.class);

	/**
	 * The command bus to register on
	 */
	private transient CommandBus commandBus;

	/**
	 * The domain locator to be passed to command handlers
	 */
	private transient DomainLocator domainLocator;

	// ------------------------------------------------------------------------

	/**
	 *
	 * Convenient class for Axon command bus subscription proper dynamic typing
	 *
	 * @param <C> the Kasper command type handled
	 */
	private static class CommandCastor<C extends Command> {

		private final transient Class<? extends C> payload;
		private final transient org.axonframework.commandhandling.CommandHandler handler;

		@SuppressWarnings("unchecked") // Safe by previous parent class typing
		CommandCastor(final Class<?> bean, final org.axonframework.commandhandling.CommandHandler container) {
			this.payload = (Class<? extends C>) bean;
			this.handler = container;
		}

		public Class<? extends C> getBeanClass() {
			return this.payload;
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
	public void process(final Class<?> commandHandlerClazz, final CommandHandler<?> commandHandler) {
		LOGGER.info("Subscribe to command bus : " + commandHandlerClazz.getName());

		if (AbstractCommandHandler.class.isAssignableFrom(commandHandler.getClass())) {
			((AbstractCommandHandler<?>) commandHandler).setDomainLocator(this.domainLocator);
		}
		
		//- Retrieve command type from command handler definition -------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<?>> commandClass =
				(Optional<Class<?>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(commandHandlerClazz,
						CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION);

		if (commandClass.isPresent()) {
		    // register this command handler for further use in kasper components
			domainLocator.registerHandler(commandHandler);
            
			//- Dynamic type command class and command handler for Axon -------
			final CommandCastor<Command> castor =
					new CommandCastor<>(commandClass.get(), commandHandler);

			//- Subscribe the handler to this command type (Axon) -------------
			this.commandBus.subscribe(castor.getBeanClass().getName(), castor.getContainerClass());

		} else {
			throw new KasperException("Unable to determine Command class for handler " + commandHandlerClazz.getName());
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * @param commandBus the command bus to register command handlers on
	 */
	public void setCommandBus(final CommandBus commandBus) {
		this.commandBus = Preconditions.checkNotNull(commandBus);
	}

	/**
	 * @param domainLocator the domain locator to inject on command handlers
	 */
	public void setDomainLocator(final DomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}

}

