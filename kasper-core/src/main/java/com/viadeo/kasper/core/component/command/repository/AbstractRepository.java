// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractRepository<ID extends KasperID, AGR extends AggregateRoot> implements Repository<ID, AGR> {

    private AxonRepositoryFacade<AGR> axonRepository;

    /**
     * The aggregate class
     */
    protected Class<AGR> aggregateClass;

    /**
     * The event store (optional)
     */
    protected final EventStoreWrapper eventStore;

    /**
     * Temporary storage of event bus for lazy assignation
     */
    protected EventBus eventBus;

    /**
     * The metric registry
     */
    private final MetricRegistry metricRegistry;

    // ------------------------------------------------------------------------

    public AbstractRepository(final MetricRegistry metricRegistry, final EventStore eventStore, final EventBus eventBus) {
        this(metricRegistry, new EventStoreWrapper().init(eventStore), eventBus);
    }

    public AbstractRepository(final MetricRegistry metricRegistry, final EventStoreWrapper eventStore, final EventBus eventBus) {
        this.metricRegistry = metricRegistry;
        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    // ------------------------------------------------------------------------

    public AxonRepositoryFacade<AGR> getAxonRepository() {
        if (null == axonRepository) {
            this.axonRepository = createAxonRepository(metricRegistry, this);
        }
        return axonRepository;
    }

    @VisibleForTesting
    protected void setAxonRepository(AxonRepositoryFacade<AGR> axonRepository) {
        this.axonRepository = axonRepository;
    }

    protected abstract AxonRepositoryFacade<AGR> createAxonRepository(
            final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository
    );

    // ------------------------------------------------------------------------

    /**
     * loads an aggregate from the repository
     *
     * @param aggregateIdentifier the aggregate identifier
     * @param expectedVersion the version of the aggregate to load or null
     *
     * @return the (optional) aggregate
     */
    protected abstract Optional<AGR> doLoad(final ID aggregateIdentifier, final Long expectedVersion);

    /**
     * saves a new (create) or existing (update) aggregate to the repository
     *
     * in case of an update, the aggregate version will be set to null
     *
     * if you override doUpdate() this method will only be called on save
     *
     * @param aggregate the aggregate to be saved on the repository
     */
    protected abstract void doSave(final AGR aggregate);

    /**
     * updates an existing aggregate to the repository
     *
     * Overrides this method if you want to clearly separate saves and updates in two methods
     *
     * @param aggregate the aggregate to be saved on the repository
     */
    protected void doUpdate(final AGR aggregate) {
        this.doSave(aggregate);
    }

    /**
     * deletes (or mark as deleted) an existing aggregate from the repository
     *
     * @param aggregate the aggregate to be deleted from the repository
     */
    protected abstract void doDelete(final AGR aggregate);

    /**
     * (Optional) indicates existence of an aggregate in the repository
     *
     * @param aggregateIdentifier the aggregate identifier
     * @return true if an aggregate exists with this id
     */
    protected boolean doHas(final ID aggregateIdentifier) {
        throw new UnsupportedOperationException("has() operation not implemented");
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<AGR> getAggregateClass() {
        if (null == aggregateClass) {
            @SuppressWarnings("unchecked") // Safe
            final Optional<Class<AGR>> entityType =
                    (Optional<Class<AGR>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                            this.getClass(), Repository.class, ENTITY_PARAMETER_POSITION));

            if ( ! entityType.isPresent()) {
                throw new KasperException("Cannot determine entity type for " + this.getClass().getName());
            }

            this.aggregateClass = entityType.get();
        }
        return aggregateClass;
    }

    @Override
    public void add(final AGR aggregate) {
        /* All aggregates must have an ID */
        if (null == aggregate.getIdentifier()) {
            throw new KasperCommandException("Aggregates must have an ID (use setID()) before saves");
        }

        getAxonRepository().add(aggregate);
    }

    @Override
    public boolean has(final ID id) {
        return doHas(id);
    }

    @Override
    public void save(final AGR aggregate) {
        getAxonRepository().save(aggregate);
    }

    @Override
    public void delete(final AGR aggregate) {
        getAxonRepository().delete(aggregate);
    }

    @Override
    public Optional<AGR> load(final ID aggregateIdentifier) {
        try {
            return Optional.fromNullable(getAxonRepository().load(aggregateIdentifier));
        } catch (AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    @Override
    public Optional<AGR> load(final ID aggregateIdentifier, final Long expectedVersion) {
        return Optional.fromNullable(getAxonRepository().load(aggregateIdentifier, expectedVersion));
    }

    @Deprecated
    public AGR loadWithException(final ID aggregateIdentifier) throws AggregateNotFoundException {
        return getAxonRepository().load(aggregateIdentifier);
    }

    @Override
    public Optional<AGR> get(final ID aggregateIdentifier) {
        return Optional.fromNullable(getAxonRepository().get(aggregateIdentifier));
    }

    @Override
    public Optional<AGR> get(final ID aggregateIdentifier, final Long expectedVersion) {
        return Optional.fromNullable(getAxonRepository().get(aggregateIdentifier, expectedVersion));
    }

    @Deprecated
    public AGR getWithException(final ID aggregateIdentifier) throws AggregateNotFoundException {
        return getAxonRepository().get(aggregateIdentifier);
    }

    @Override
    public Class<?> getRepositoryClass() {
        return getClass();
    }

    // ------------------------------------------------------------------------

    static class EventStoreWrapper implements EventStore {

        private EventStore eventStore;

        public EventStoreWrapper init(final EventStore eventStore) {
            this.eventStore = eventStore;
            return this;
        }

        @Override
        public void appendEvents(final String type, final DomainEventStream events) {
            if (eventStore != null) {
                eventStore.appendEvents(type, events);
            }
        }

        @Override
        public DomainEventStream readEvents(final String type, final Object identifier) {
            checkState(eventStore != null);
            return eventStore.readEvents(type, identifier);
        }
    }

}
