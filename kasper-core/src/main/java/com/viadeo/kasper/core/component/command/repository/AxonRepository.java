// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

public class AxonRepository<ID extends KasperID, AGR extends AggregateRoot>
    extends org.axonframework.repository.AbstractRepository<AGR>
    implements AxonRepositoryFacade<AGR>
{

    private final AbstractRepository<ID,AGR> repository;

    public AxonRepository(AbstractRepository<ID, AGR> repository) {
        super(repository.getAggregateClass());
        this.repository = checkNotNull(repository);
    }

    @Override
    public void save(AGR aggregate) {
        doSave(aggregate);
    }

    @Override
    public void update(AGR aggregate) {
        doUpdate(aggregate);
    }

    @Override
    public void delete(AGR aggregate) {
        doDelete(aggregate);
    }

    @Override
    public AGR load(Object aggregateIdentifier) {
        return load(aggregateIdentifier, null);
    }

    @Override
    public AGR get(Object aggregateIdentifier, Long expectedVersion) {
        return doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    public AGR get(Object aggregateIdentifier) {
        return get(aggregateIdentifier, null);
    }

    @Override
    protected void doSave(AGR aggregate) {
        checkNotNull(aggregate);

        if (aggregate.getVersion() != null) {
            doUpdate(aggregate);
        } else {
            repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());
            aggregate.setVersion(1L);
            repository.doSave(aggregate);
        }
    }

    protected void doUpdate(AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (aggregate.getVersion() != null) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doUpdate(aggregate);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AGR doLoad(Object aggregateIdentifier, Long expectedVersion) {
        checkNotNull(aggregateIdentifier);

        final Optional<AGR> optionalAggregate = repository.doLoad((ID) aggregateIdentifier, expectedVersion);

        if (!optionalAggregate.isPresent()) {
            throw new AggregateNotFoundException(aggregateIdentifier, "Failed to load an aggregate");
        }

        if (optionalAggregate.isPresent() && optionalAggregate.get().getVersion() == null) {
            optionalAggregate.get().setVersion(0L);
        }

        return optionalAggregate.get();
    }

    @Override
    protected void doDelete(AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (aggregate.getVersion() != null) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doDelete(aggregate);
    }

}
