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
 * Base implementation for an repository.
 *
 * @param <ID> the aggregate id
 * @param <AGR> AggregateRoot
 */
public abstract class BaseRepository<ID extends KasperID, AGR extends AggregateRoot> extends AbstractRepository<ID,AGR> {

    protected BaseRepository(final EventBus eventBus) {
        this(KasperMetrics.getMetricRegistry(), eventBus);
    }

    protected BaseRepository(final MetricRegistry metricRegistry, final EventBus eventBus) {
        super(metricRegistry, (EventStore)null, eventBus);
        getAxonRepository().setEventBus(eventBus);
    }

    @Override
    protected AxonRepository<ID,AGR> createAxonRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        return new AxonRepository<>(new MeasuredRepository<>(metricRegistry, repository));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AxonRepository<ID,AGR> getAxonRepository() {
        return (AxonRepository<ID,AGR>) super.getAxonRepository();
    }

}
