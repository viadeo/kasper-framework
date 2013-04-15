// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.viadeo.kasper.context.impl.CurrentContext;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.ICommandMessage;
import com.viadeo.kasper.cqrs.command.ICommandResult;
import com.viadeo.kasper.event.exceptions.KasperEventException;
import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 *
 * @param <C> Command
 */
public abstract class AbstractCommandHandler<C extends ICommand> 
		implements ICommandHandler<C> {

	final private static Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);

	// ------------------------------------------------------------------------
	
	/**
	 * Wrapper for Axon command handling
	 * 
	 * @see org.axonframework.commandhandling.CommandHandler#handle(org.axonframework.commandhandling.CommandMessage, org.axonframework.unitofwork.UnitOfWork)
	 */
	@SuppressWarnings("deprecation") // Controlled use of KasperErrorCommandResult
	@Override
	public Object handle(final CommandMessage<C> message, final UnitOfWork uow)
			throws Throwable {
		final ICommandMessage<C> kmessage = new KasperCommandMessage<C>(message);
		CurrentContext.set(kmessage.getContext());

		AbstractCommandHandler.LOGGER.debug("Handle command " + message.getPayload().getClass().getSimpleName());

		ICommandResult ret;
		try {
			try {
			ret = this.handle(kmessage);
			} catch (final UnsupportedOperationException e) {
				try {
					ret = this.handle(kmessage, uow);
				} catch (final UnsupportedOperationException e2) {
					ret = this.handle((C) message.getPayload());
				}
			}
		} catch (final KasperRuntimeException e) {
			ret = new KasperErrorCommandResult(e); // Reserved usage
			if (uow.isStarted()) {
				uow.rollback(e);
				uow.start();
			}
		} catch (final KasperEventException e) {
			ret = new KasperErrorCommandResult(e); // Reserved usage
			if (uow.isStarted()) {
				uow.rollback(e);
				uow.start();
			}
		} catch (final IllegalArgumentException e) {
			AbstractCommandHandler.LOGGER.error("Illegal argument exception occured when handling command " + message.getCommandName(), e);
			ret = new KasperErrorCommandResult(e); // Reserved usage
			if (uow.isStarted()) {
				uow.rollback(e);
				uow.start();
			}
		} catch (final RuntimeException e) {
			AbstractCommandHandler.LOGGER.error("Runtime exception occured when handling command " + message.getCommandName(), e);
			throw e;
		}

		return ret;
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param message the command handler encapsulating message
	 * @param uow Axon unit of work
	 * @return the command result
	 * @throws KasperEventException
	 */
	public ICommandResult handle(final ICommandMessage<C> message, final UnitOfWork uow) throws KasperEventException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @param message the command handler encapsulating message
	 * @return the command result
	 * @throws KasperEventException
	 */
	public ICommandResult handle(final ICommandMessage<C> message) throws KasperEventException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param command The command to handle
	 * @return
	 * @throws KasperEventException
	 */
	public ICommandResult handle(final C command) throws KasperEventException {
		throw new UnsupportedOperationException();
	}	
	
}
