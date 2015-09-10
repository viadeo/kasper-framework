// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.context.CurrentContext;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.CurrentUnitOfWork;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for an auto wired Kasper command handler.
 *
 * @param <C> Command
 */
public abstract class AutowiredCommandHandler<C extends Command>
        extends BaseCommandHandler<C>
        implements WirableCommandHandler<C>
{

    private transient EventBus eventBus;
    private transient CommandGateway commandGateway;
    protected transient RepositoryManager repositoryManager;

    // ------------------------------------------------------------------------

    /**
     * @param context The context
     * @param command The command to handle
     * @return the command response
     */
    @Override
    public CommandResponse handle(Context context, C command) {
        try {
            return handle(command);
        } catch (final UnsupportedOperationException e) {
            try {
                return handle(new CommandMessage<>(context, command));
            } catch (final UnsupportedOperationException e1) {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * @param command The command to handle
     * @return the command response
     */
    public CommandResponse handle(final C command) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param message The command message to handle
     * @return the command response
     */
    public CommandResponse handle(final CommandMessage<C> message) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event using the current unit of work
     *
     * @param event The event to be scheduled for publication to the unit of work
     */
    public void publish(final Event event) {
        final EventMessage axonMessage = GenericEventMessage.asEventMessage(event);

        if (CurrentUnitOfWork.isStarted()) {
            CurrentUnitOfWork.get().publishEvent(axonMessage, eventBus);
        } else {
            throw new KasperCommandException("UnitOfWork is not started when trying to publish event");
        }
    }

    // ------------------------------------------------------------------------

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

    @Override
    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public void setRepositoryManager(final RepositoryManager repositoryManager) {
        this.repositoryManager = checkNotNull(repositoryManager);
    }

    @Override
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

}
