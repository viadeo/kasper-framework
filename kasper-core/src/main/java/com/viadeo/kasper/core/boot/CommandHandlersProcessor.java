// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.core.boot;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractEntityCommandHandler;
import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.locators.ICommandHandlersLocator;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 * Process Kasper command handlers dynamic registration at kasper platform boot
 *
 * @see XKasperCommandHandler
 */
public class CommandHandlersProcessor extends AbstractSingletonAnnotationProcessor<XKasperCommandHandler, ICommandHandler<?>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlersProcessor.class);

	/**
	 * The command bus to register on
	 */
	private transient CommandBus commandBus;

	/**
	 * The domain locator to be passed to command handlers
	 */
	private transient IDomainLocator domainLocator;
	
	private transient ICommandHandlersLocator commandHandlersLocator;

	// ------------------------------------------------------------------------

	/**
	 *
	 * Convenient class for Axon command bus subscription proper dynamic typing
	 *
	 * @param <C> the Kasper command type handled
	 */
	private static class CommandCastor<C extends ICommand> {

		private final transient Class<? extends C> payload;
		private final transient CommandHandler<? super C> handler;

		@SuppressWarnings("unchecked") // Safe by previous parent class typing
		CommandCastor(final Class<?> bean, final CommandHandler<?> container) {
			this.payload = (Class<? extends C>) bean;
			this.handler = (CommandHandler<? super C>) container;
		}

		public Class<? extends C> getBeanClass() {
			return this.payload;
		}

		public CommandHandler<? super C> getContainerClass() {
			return this.handler;
		}
	}

	//-------------------------------------------------------------------------

	/**
	 * Process Kasper command handlers
	 * 
	 * @see ICommandHandler
	 * @see com.viadeo.kasper.core.boot.IAnnotationProcessor#process(java.lang.Class)
	 */
	@Override
	public void process(final Class<?> commandHandlerClazz, final ICommandHandler<?> commandHandler) {
		LOGGER.info("Subscribe to command bus : " + commandHandlerClazz.getName());

		if (AbstractEntityCommandHandler.class.isAssignableFrom(commandHandler.getClass())) {
			((AbstractEntityCommandHandler<?, ?>) commandHandler).setDomainLocator(this.domainLocator);
		}
		
		//- Retrieve command type from command handler definition -------------
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<?>> commandClass =
				(Optional<Class<?>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(commandHandlerClazz,
						ICommandHandler.class, ICommandHandler.COMMAND_PARAMETER_POSITION);

		if (commandClass.isPresent()) {
		    // register this command handler for further use in kasper components
		    commandHandlersLocator.registerHandler(commandHandler);
            
			//- Dynamic type command class and command handler for Axon -------
			final CommandCastor<ICommand> castor =
					new CommandCastor<ICommand>(commandClass.get(), commandHandler);

			//- Subscribe the handler to this command type (Axon) -------------
			this.commandBus.subscribe(castor.getBeanClass().getName(), castor.getContainerClass());

		} else {
			throw new KasperRuntimeException("Unable to determine Command class for handler " + commandHandlerClazz.getName());
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
	public void setDomainLocator(final IDomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}

    public void setCommandHandlersLocator(ICommandHandlersLocator commandHandlersLocator) {
        this.commandHandlersLocator = Preconditions.checkNotNull(commandHandlersLocator);
    }

}

