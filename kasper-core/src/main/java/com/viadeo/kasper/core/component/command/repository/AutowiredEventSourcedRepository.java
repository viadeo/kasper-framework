// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for an auto wired event sourced repository.
 *
 * @param <ID> the aggregate id
 * @param <AGR> AggregateRoot
 */
public abstract class AutowiredEventSourcedRepository<ID extends KasperID, AGR extends AggregateRoot>
        extends BaseEventSourcedRepository<ID,AGR>
        implements WirableRepository
{

    private AxonEventSourcedRepository<ID,AGR> axonEventSourcedRepository;

    protected AutowiredEventSourcedRepository() {
        super(null, null);
    }

    @Override
    protected AxonEventSourcedRepository<ID,AGR> createAxonRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        this.axonEventSourcedRepository = super.createAxonRepository(metricRegistry, repository);
        return axonEventSourcedRepository;
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        checkNotNull(eventBus);
        getAxonRepository().setEventBus(eventBus);
        this.eventBus = eventBus;
    }

    @Override
    public void setEventStore(EventStore eventStore) {
        checkNotNull(eventStore);
        this.eventStore.init(eventStore);
    }

    @Override
    protected Optional<AGR> doLoad(ID aggregateIdentifier, Long expectedVersion) {
        try {
            return Optional.of(
                    this.axonEventSourcedRepository.doRealLoad(aggregateIdentifier, expectedVersion)
            );
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    @Override
    protected void doSave(AGR aggregate) {
        this.axonEventSourcedRepository.doRealSaveWithLock(aggregate);
    }

    @Override
    protected void doDelete(AGR aggregate) {
        this.axonEventSourcedRepository.doRealDeleteWithLock(aggregate);
    }
}
