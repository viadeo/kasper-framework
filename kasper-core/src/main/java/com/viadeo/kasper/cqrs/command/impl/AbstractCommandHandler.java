// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.*;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @param <C> Command
 */
public abstract class AbstractCommandHandler<C extends Command> implements CommandHandler<C> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);
    private static final MetricRegistry metrics = KasperMetrics.getRegistry();

    private transient DomainLocator domainLocator;

    // ------------------------------------------------------------------------

    /**
     * Wrapper for Axon command handling
     * 
     * @see org.axonframework.commandhandling.CommandHandler#handle(org.axonframework.commandhandling.CommandMessage,
     * org.axonframework.unitofwork.UnitOfWork)
     */
    @Override
    public final Object handle(final CommandMessage<C> message, final UnitOfWork uow) throws Throwable {
        final KasperCommandMessage<C> kmessage = new DefaultKasperCommandMessage<>(message);
        CurrentContext.set(kmessage.getContext());

        final Class<?> commandClass = message.getPayload().getClass();

        AbstractCommandHandler.LOGGER.debug("Handle command " + commandClass.getSimpleName());

        /* Start timer */
        final Timer.Context timer = metrics.timer(name(commandClass, "requests-time")).time();

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
            LOGGER.error("Error command [{}]", commandClass, e);

            /* rollback uow on failure */
            if (uow.isStarted()) {
                uow.rollback(e);
                uow.start();
            }
            /*
             * FIXME should we transform to a command result or just rollback and propagate the exception as is
             * let's propagate the error as is and keep CommandResult for business operation result (success and failure) ?
             */

            /* Stop timer on error and propage exception */
            timer.close();
            throw e;
        }

        /* Monitor the request calls */
        final long time = timer.stop();
        metrics.histogram(name(CommandGateway.class, "requests-times")).update(time);
        metrics.histogram(name(commandClass, "requests-times")).update(time);

        metrics.meter(name(CommandGateway.class, "requests")).mark();
        metrics.meter(name(commandClass, "requests")).mark();
        if (ret.isError()) {
            metrics.meter(name(CommandGateway.class, "errors")).mark();
            metrics.meter(name(commandClass, "errors")).mark();
        }

        return ret;
    }

    // ------------------------------------------------------------------------

    /**
     * @param message the command handler encapsulating message
     * @param uow Axon unit of work
     * @return the command result
     * @throws Exception
     */
    public CommandResult handle(final KasperCommandMessage<C> message, final UnitOfWork uow) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param message the command handler encapsulating message
     * @return the command result
     * @throws Exception
     */
    public CommandResult handle(final KasperCommandMessage<C> message) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param command The command to handle
     * @throws Exception
     */
    public CommandResult handle(final C command) throws Exception {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * @param domainLocator
     */
    public void setDomainLocator(final DomainLocator domainLocator) {
        this.domainLocator = domainLocator;
    }

    protected DomainLocator getDomainLocator() {
        return this.domainLocator;
    }

}
