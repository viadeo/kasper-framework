// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.viadeo.kasper.context.impl.CurrentContext;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.ICommandMessage;
import com.viadeo.kasper.cqrs.command.CommandResult;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <C> Command
 */
public abstract class AbstractCommandHandler<C extends ICommand> implements ICommandHandler<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);

    /**
     * Wrapper for Axon command handling
     * 
     * @see org.axonframework.commandhandling.CommandHandler#handle(org.axonframework.commandhandling.CommandMessage,
     * org.axonframework.unitofwork.UnitOfWork)
     */
    @Override
    final public Object handle(final CommandMessage<C> message, final UnitOfWork uow) throws Throwable {
        final ICommandMessage<C> kmessage = new KasperCommandMessage<>(message);
        CurrentContext.set(kmessage.getContext());

        AbstractCommandHandler.LOGGER.debug("Handle command " + message.getPayload().getClass().getSimpleName());

        CommandResult ret;
        try {
            try {
                ret = this.handle(kmessage);
            } catch (final UnsupportedOperationException e) {
                try {
                    ret = this.handle(kmessage, uow);
                } catch (final UnsupportedOperationException e2) {
                    ret = this.handle(message.getPayload());
                }
            }
        } catch (final Exception e) {
            // FIXME I let the log for the moment, but I am not convinced that the low level layer should log errors
            // IMO this should be done only once at a top level (for example auto expo).
            LOGGER.error("Error command [{}]", message.getPayload().getClass(), e);
            
            if (uow.isStarted()) {
                uow.rollback(e);
                uow.start();
            }
            // FIXME I hesitate, should we transform to a command result or just rollback and propagate the exception as is
            // let's propagate the error as is and keep CommandResult for business operation result (success and failure).
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
    public CommandResult handle(final ICommandMessage<C> message, final UnitOfWork uow) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param message the command handler encapsulating message
     * @return the command result
     * @throws KasperEventException
     */
    public CommandResult handle(final ICommandMessage<C> message) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param command The command to handle
     * @throws KasperEventException
     */
    public CommandResult handle(final C command) throws Exception {
        throw new UnsupportedOperationException();
    }

}
