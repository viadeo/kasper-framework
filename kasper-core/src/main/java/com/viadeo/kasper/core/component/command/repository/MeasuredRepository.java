// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * This implementation of <code>Repository</code> allows to add metrics.
 *
 * @param <ID> the aggregate id
 * @param <AGR> the aggregate
 */
public class MeasuredRepository<ID extends KasperID, AGR extends AggregateRoot>
        extends AbstractRepository<ID, AGR>
        implements Repository<ID, AGR>
{

    private static final String GLOBAL_METER_SAVES_NAME = name(Repository.class, "saves");
    private static final String GLOBAL_METER_SAVE_ERRORS_NAME = name(Repository.class, "save-errors");

    private static final String GLOBAL_METER_LOADS_NAME = name(Repository.class, "loads");
    private static final String GLOBAL_METER_LOAD_ERRORS_NAME = name(Repository.class, "load-errors");

    private static final String GLOBAL_METER_DELETES_NAME = name(Repository.class, "deletes");
    private static final String GLOBAL_METER_DELETE_ERRORS_NAME = name(Repository.class, "delete-errors");

    // ------------------------------------------------------------------------

    private final String timerSaveTimeName;
    private final String meterSaveErrorsName;
    private final String timerLoadTimeName;
    private final String meterLoadErrorsName;
    private final String timerDeleteTimeName;
    private final String meterDeleteErrorsName;

    private final MetricRegistry metricRegistry;
    private final AbstractRepository<ID,AGR> repository;

    public MeasuredRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        super(metricRegistry, repository.eventStore, repository.eventBus);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.repository = checkNotNull(repository);

        this.timerSaveTimeName = name(repository.getRepositoryClass(), "save-time");
        this.meterSaveErrorsName = name(repository.getRepositoryClass(), "save-errors");
        this.timerLoadTimeName = name(repository.getRepositoryClass(), "load-time");
        this.meterLoadErrorsName = name(repository.getRepositoryClass(), "load-errors");
        this.timerDeleteTimeName = name(repository.getRepositoryClass(), "delete-time");
        this.meterDeleteErrorsName = name(repository.getRepositoryClass(), "delete-errors");
    }

    protected AxonRepositoryFacade<AGR> createAxonRepository(
            final MetricRegistry metricRegistry,
            final AbstractRepository<ID,AGR> repository
    ) {
        return null;
    }

    @Override
    protected void doUpdate(AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerSaveTimeName).time();

        try {
            repository.doUpdate(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_SAVE_ERRORS_NAME).mark();
            metricRegistry.meter(meterSaveErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_SAVES_NAME).mark();
        }
    }

    @Override
    protected void doSave(AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerSaveTimeName).time();

        try {
            repository.doSave(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_SAVE_ERRORS_NAME).mark();
            metricRegistry.meter(meterSaveErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_SAVES_NAME).mark();
        }
    }

    @Override
    protected Optional<AGR>  doLoad(ID aggregateIdentifier, Long expectedVersion) {
        final Timer.Context timer = metricRegistry.timer(timerLoadTimeName).time();

        final Optional<AGR> agr;

        try {
            agr = repository.doLoad(aggregateIdentifier, expectedVersion);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_LOAD_ERRORS_NAME).mark();
            metricRegistry.meter(meterLoadErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_LOADS_NAME).mark();
        }

        return agr;
    }

    @Override
    protected void doDelete(AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerDeleteTimeName).time();

        try {
            repository.doDelete(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_DELETE_ERRORS_NAME).mark();
            metricRegistry.meter(meterDeleteErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_DELETES_NAME).mark();
        }
    }

    @Override
    public Class<AGR> getAggregateClass() {
        return repository.getAggregateClass();
    }

    @Override
    public void save(AGR aggregate) {
        repository.save(aggregate);
    }

    @Override
    public Optional<AGR> load(ID aggregateIdentifier, Long expectedVersion) {
        return repository.load(aggregateIdentifier, expectedVersion);
    }

    @Override
    public Optional<AGR> load(ID aggregateIdentifier) {
        return repository.load(aggregateIdentifier);
    }

    @Override
    public void delete(AGR aggregate) {
        repository.delete(aggregate);
    }

    @Override
    public void add(AGR aggregate) {
        repository.add(aggregate);
    }

    @Override
    public boolean has(ID id) {
        return repository.has(id);
    }

    @Override
    public Optional<AGR> get(ID aggregateIdentifier, Long expectedVersion) {
        return repository.get(aggregateIdentifier, expectedVersion);
    }

    @Override
    public Optional<AGR> get(ID aggregateIdentifier) {
        return repository.get(aggregateIdentifier);
    }

    @Override
    public Class<?> getRepositoryClass() {
        return repository.getRepositoryClass();
    }

}
