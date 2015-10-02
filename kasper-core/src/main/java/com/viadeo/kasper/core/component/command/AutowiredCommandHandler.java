// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.repository.Repository;
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

    /**
     * Get the related repository of the specified entity class
     *
     * @param entityClass the class of the entity
     * @param <REPO> the type of repository
     * @return the entity repository
     */
    public <REPO extends Repository> Optional<REPO> getRepositoryOf(final Class<? extends AggregateRoot> entityClass) {
        if (null == repositoryManager) {
            throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
        }

        return repositoryManager.getEntityRepository(entityClass);
    }

}
