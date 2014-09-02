// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class MetricsRepositoryFacade<AGR extends AggregateRoot> extends RepositoryFacade<AGR> {

    private static final String GLOBAL_HISTO_SAVE_TIMES_NAME = name(IRepository.class, "save-times");
    private static final String GLOBAL_METER_SAVES_NAME = name(IRepository.class, "saves");
    private static final String GLOBAL_METER_SAVE_ERRORS_NAME = name(IRepository.class, "save-errors");

    private static final String GLOBAL_HISTO_LOAD_TIMES_NAME = name(IRepository.class, "load-times");
    private static final String GLOBAL_METER_LOADS_NAME = name(IRepository.class, "loads");
    private static final String GLOBAL_METER_LOAD_ERRORS_NAME = name(IRepository.class, "load-errors");

    private static final String GLOBAL_HISTO_DELETE_TIMES_NAME = name(IRepository.class, "delete-times");
    private static final String GLOBAL_METER_DELETES_NAME = name(IRepository.class, "deletes");
    private static final String GLOBAL_METER_DELETE_ERRORS_NAME = name(IRepository.class, "delete-errors");

    // ------------------------------------------------------------------------

    private final String timerSaveTimeName;
    private final String meterSaveErrorsName;
    private final String histoSavesTimesName;
    private final String meterSavesName;

    private final String timerLoadTimeName;
    private final String meterLoadErrorsName;
    private final String histoLoadsTimesName;
    private final String meterLoadsName;

    private final String timerDeleteTimeName;
    private final String meterDeleteErrorsName;
    private final String histoDeletesTimesName;
    private final String meterDeletesName;

    // ------------------------------------------------------------------------

    MetricsRepositoryFacade(final Repository<AGR> kasperRepository) {
        super(kasperRepository);
        Class kasperRepositoryClass = checkNotNull(kasperRepository).getClass();

        this.timerSaveTimeName = name(kasperRepositoryClass, "save-time");
        this.meterSaveErrorsName = name(kasperRepositoryClass, "save-errors");
        this.histoSavesTimesName = name(kasperRepositoryClass, "save-times");
        this.meterSavesName = name(kasperRepositoryClass, "saves");

        this.timerLoadTimeName = name(kasperRepositoryClass, "load-time");
        this.meterLoadErrorsName = name(kasperRepositoryClass, "load-errors");
        this.histoLoadsTimesName = name(kasperRepositoryClass, "load-times");
        this.meterLoadsName = name(kasperRepositoryClass, "loads");

        this.timerDeleteTimeName = name(kasperRepositoryClass, "delete-time");
        this.meterDeleteErrorsName = name(kasperRepositoryClass, "delete-errors");
        this.histoDeletesTimesName = name(kasperRepositoryClass, "delete-times");
        this.meterDeletesName = name(kasperRepositoryClass, "deletes");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doSave(final AGR aggregate) {
        final Timer.Context timer = getMetricRegistry().timer(timerSaveTimeName).time();

        try {
            super.doSave(aggregate);

        } catch (final RuntimeException e) {
            getMetricRegistry().meter(GLOBAL_METER_SAVE_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterSaveErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            getMetricRegistry().histogram(GLOBAL_HISTO_SAVE_TIMES_NAME).update(time);
            getMetricRegistry().histogram(histoSavesTimesName).update(time);

            getMetricRegistry().meter(GLOBAL_METER_SAVES_NAME).mark();
            getMetricRegistry().meter(meterSavesName).mark();
        }

    }

    // ------------------------------------------------------------------------

    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        final Timer.Context timer = getMetricRegistry().timer(timerLoadTimeName).time();

        final AGR agr;

        try {
            agr = super.doLoad(aggregateIdentifier, expectedVersion);

        } catch (final RuntimeException e) {
            getMetricRegistry().meter(GLOBAL_METER_LOAD_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterLoadErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            getMetricRegistry().histogram(GLOBAL_HISTO_LOAD_TIMES_NAME).update(time);
            getMetricRegistry().histogram(histoLoadsTimesName).update(time);

            getMetricRegistry().meter(GLOBAL_METER_LOADS_NAME).mark();
            getMetricRegistry().meter(meterLoadsName).mark();
        }

        return agr;
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doDelete(final AGR aggregate) {
        final Timer.Context timer = getMetricRegistry().timer(timerDeleteTimeName).time();

        try {
            super.doDelete(aggregate);

        } catch (final RuntimeException e) {
            getMetricRegistry().meter(GLOBAL_METER_DELETE_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterDeleteErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            getMetricRegistry().histogram(GLOBAL_HISTO_DELETE_TIMES_NAME).update(time);
            getMetricRegistry().histogram(histoDeletesTimesName).update(time);

            getMetricRegistry().meter(GLOBAL_METER_DELETES_NAME).mark();
            getMetricRegistry().meter(meterDeletesName).mark();
        }

    }

}
