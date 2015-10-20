// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for an auto wired repository.
 *
 * @param <ID> the aggregate id
 * @param <AGR> AggregateRoot
 */
public abstract class AutowiredRepository<ID extends KasperID, AGR extends AggregateRoot>
    extends BaseRepository<ID,AGR>
    implements WirableRepository
{
    protected AutowiredRepository() {
        super(null);
    }

    @Override
    public void setEventBus(final EventBus eventBus) {
        checkNotNull(eventBus);
        getAxonRepository().setEventBus(eventBus);
        this.eventBus = eventBus;
    }

    @Override
    public void setEventStore(final EventStore eventStore) {
        checkNotNull(eventStore);
        this.eventStore.init(eventStore);
    }

    // FIXME remove as soon as this method
    public void init() {}

}
