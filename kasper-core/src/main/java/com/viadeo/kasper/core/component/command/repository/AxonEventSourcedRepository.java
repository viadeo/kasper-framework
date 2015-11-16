// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;

import static com.google.common.base.Preconditions.checkNotNull;

public class AxonEventSourcedRepository<ID extends KasperID, AGR extends AggregateRoot>
        extends EventSourcingRepository<AGR>
        implements AxonRepositoryFacade<AGR>
{
    private final AbstractRepository<ID,AGR> repository;

    // ------------------------------------------------------------------------

    public AxonEventSourcedRepository(final AbstractRepository<ID, AGR> repository, final EventStore eventStore) {
        super(repository.getAggregateClass(), eventStore);
        this.repository = checkNotNull(repository);
    }

    // ------------------------------------------------------------------------

    @Override
    public void save(final AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if ((null != aggregate.getVersion()) && (aggregate.getVersion() > 0L)) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
            repository.doUpdate(aggregate);
        } else {
            aggregate.setVersion(1L);
            repository.doSave(aggregate);
        }
    }

    @Override
    public void update(final AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doUpdate(aggregate);
    }

    @Override
    public void delete(final AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doDelete(aggregate);
    }

    @Override
    public AGR get(final Object aggregateIdentifier, Long expectedVersion) {
        final AGR agr = doLoad(aggregateIdentifier, expectedVersion);
        if (null != agr &&  null != agr.getVersion()) {
            agr.setVersion(0L);
        }
        return agr;
    }

    @Override
    public AGR get(final Object aggregateIdentifier) {
        return get(aggregateIdentifier, null);
    }

    public void doRealSaveWithLock(final AGR aggregate) {
        super.doSaveWithLock(aggregate);
    }

    public AGR doRealLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return super.doLoad(aggregateIdentifier, expectedVersion);
    }

    public void doRealDeleteWithLock(final AGR aggregate) {
        super.doDeleteWithLock(aggregate);
    }

}
