// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.*;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * @param <C> Command
 */
public abstract class AbstractCommandHandler<C extends Command> implements CommandHandler<C> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);
    private static final MetricRegistry metrics = KasperMetrics.getRegistry();

    private static final Timer metricClassTimer = metrics.timer(name(CommandGateway.class, "requests-time"));
    private static final Histogram metricClassRequestsTimes = metrics.histogram(name(CommandGateway.class, "requests-times"));
    private static final Meter metricClassRequests = metrics.meter(name(CommandGateway.class, "requests"));
    private static final Meter metricClassErrors = metrics.meter(name(CommandGateway.class, "errors"));

    private final Timer metricTimer;
    private final Histogram metricRequestsTimes;
    private final Meter metricRequests;
    private final Meter metricErrors;

    private transient DomainLocator domainLocator;

    // ------------------------------------------------------------------------

    protected AbstractCommandHandler() {
        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<C>> commandClass =
                (Optional<Class<C>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                        this.getClass(), CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION));

        if (!commandClass.isPresent()) {
            throw new KasperCommandException("Unable to determine Command class for "
                    + this.getClass().getSimpleName());
        }

        metricTimer = metrics.timer(name(commandClass.get(), "requests-time"));
        metricRequestsTimes = metrics.histogram(name(commandClass.get(), "requests-times"));
        metricRequests = metrics.meter(name(commandClass.get(), "requests"));
        metricErrors = metrics.meter(name(commandClass.get(), "errors"));
    }

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
        final Timer.Context classTimer = metricClassTimer.time();
        final Timer.Context timer = metricTimer.time();

        CommandResult ret = null;
        RuntimeException runtimeException = null;
        try {

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

            } catch (final RuntimeException e) {
                runtimeException = e;
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
            classTimer.close();
            timer.close();
            throw e;
        }

        if (null == runtimeException) {
            checkNotNull(ret);
        }

        /* Monitor the request calls */
        timer.close();
        final long time = classTimer.stop();
        metricClassRequestsTimes.update(time);
        metricRequestsTimes.update(time);
        metricClassRequests.mark();
        metricRequests.mark();
        if ((null != runtimeException) || ret.isError()) {
            metricClassErrors.mark();
            metricErrors.mark();
        }

        if (null != runtimeException) {
            throw runtimeException;
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
