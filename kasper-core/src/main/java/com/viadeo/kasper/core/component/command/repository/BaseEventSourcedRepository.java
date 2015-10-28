// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;

/**
 * Base implementation for an event sourced repository.
 *
 * @param <ID> the aggregate id
 * @param <AGR> AggregateRoot
 */
public abstract class BaseEventSourcedRepository<ID extends KasperID,AGR extends AggregateRoot>
        extends AbstractRepository<ID,AGR>
{

    protected BaseEventSourcedRepository(final EventStore eventStore, final EventBus eventBus) {
        this(KasperMetrics.getMetricRegistry(), eventStore, eventBus);
    }

    protected BaseEventSourcedRepository(final MetricRegistry metricRegistry, final EventStore eventStore, final EventBus eventBus) {
        super(metricRegistry, eventStore, eventBus);
    }

    // ------------------------------------------------------------------------

    @Override
    protected AxonEventSourcedRepository<ID,AGR> createAxonRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        return new AxonEventSourcedRepository<>(
                new MeasuredRepository<>(metricRegistry, repository),
                eventStore
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AxonEventSourcedRepository<ID,AGR> getAxonRepository() {
        return (AxonEventSourcedRepository<ID, AGR>) super.getAxonRepository();
    }

}
