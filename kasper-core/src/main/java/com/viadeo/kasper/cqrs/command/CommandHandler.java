// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * @param <C> Command
 */
public abstract class CommandHandler<C extends Command>
        implements  org.axonframework.commandhandling.CommandHandler<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    public static final String GLOBAL_TIMER_REQUESTS_TIME_NAME = name(CommandGateway.class, "requests-time");
    public static final String GLOBAL_METER_REQUESTS_NAME = name(CommandGateway.class, "requests");
    public static final String GLOBAL_METER_ERRORS_NAME = name(CommandGateway.class, "errors");

    /**
     * Generic parameter position for the handled command
     */
    public static final int COMMAND_PARAMETER_POSITION = 0;

    private final Class<C> commandClass;

    private transient EventBus eventBus;
    private transient CommandGateway commandGateway;
    protected transient RepositoryManager repositoryManager;

    // ------------------------------------------------------------------------

    protected CommandHandler() {
        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<C>> commandClass =
                (Optional<Class<C>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                        this.getClass(), CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION));

        if (!commandClass.isPresent()) {
            throw new KasperCommandException("Unable to determine Command class for "
                    + this.getClass().getSimpleName());
        }

        this.commandClass = commandClass.get();
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
        final KasperCommandMessage<C> kmessage = new KasperCommandMessage<>(message);
        CurrentContext.set(kmessage.getContext());

        CommandHandler.LOGGER.debug("Handle command " + commandClass.getSimpleName());

        final String timerRequestsTimeName = name(this.commandClass, "requests-time");
        final String meterErrorsName = name(this.commandClass, "errors");
        final String meterRequestsName = name(this.commandClass, "requests");

        /* Start timer */
        final Timer.Context classTimer = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_TIME_NAME).time();
        final Timer.Context timer = getMetricRegistry().timer(timerRequestsTimeName).time();

        CommandResponse ret = null;
        Exception exception = null;
        boolean isError = false;

        try {

            try {
                ret = this.handle(kmessage);
            } catch (final UnsupportedOperationException e) {
                try {
                    ret = this.handle(message.getPayload());
                } catch (final UnsupportedOperationException e2) {
                    ret = this.handle(kmessage, uow);
                }
            }

            if ( ! ret.isOK()) {
                isError = true;
            }

        } catch (final ConflictingAggregateVersionException e) {
            LOGGER.error("Error command [{}]", commandClass, e);
            isError = true;

            /**
             * Conflicting version encountered : generate a CONFLICT error
             */
            ret = CommandResponse.error(CoreReasonCode.CONFLICT, e.getMessage());

        } catch (final RuntimeException e) {
            LOGGER.error("Error command [{}]", commandClass, e);
            exception = e;
            isError = true;

        } finally {
            classTimer.close();
            timer.close();

            /* rollback uow on failure */
            if (isError && uow.isStarted()) {
                if (null != exception) {
                    uow.rollback(exception);
                } else {
                    uow.rollback();
                }
                uow.start();
            }
        }

        if (null == exception) {
            checkNotNull(ret);
        }

        /* Monitor the request calls */
        getMetricRegistry().meter(GLOBAL_METER_REQUESTS_NAME).mark();
        getMetricRegistry().meter(meterRequestsName).mark();

        if (null != exception) {
            getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterErrorsName).mark();
        }

        if (null != exception) {
            throw exception;
        } else {
            return ret;
        }

    }

    // ------------------------------------------------------------------------

    /**
     * @param message the command handler encapsulating message
     * @param uow Axon unit of work
     * @return the command response
     * @throws Exception
     */
    public CommandResponse handle(final KasperCommandMessage<C> message, final UnitOfWork uow) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param message the command handler encapsulating message
     * @return the command response
     * @throws Exception
     */
    public CommandResponse handle(final KasperCommandMessage<C> message) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @param command The command to handle
     * @throws Exception
     */
    public CommandResponse handle(final C command) throws Exception {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event using the current unit of work
     *
     * @param event The event to be scheduled for publication to the unit of work
     */
    public void publish(final IEvent event) {
        final EventMessage axonMessage = GenericEventMessage.asEventMessage(event);
        if (CurrentUnitOfWork.isStarted()) {
            CurrentUnitOfWork.get().publishEvent(axonMessage, eventBus);
        } else {
            throw new KasperCommandException("UnitOfWork is not started when trying to publish event");
        }
    }

    // ------------------------------------------------------------------------

    public Class<C> getCommandClass() {
        return commandClass;
    }

    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    public Context getContext() {
        if (CurrentContext.value().isPresent()) {
            return CurrentContext.value().get();
        }
        throw new KasperCommandException("Unexpected condition : no context was set during command handling");
    }

    // ------------------------------------------------------------------------

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    public void setRepositoryManager(final RepositoryManager repositoryManager) {
        this.repositoryManager = checkNotNull(repositoryManager);
    }

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

}
