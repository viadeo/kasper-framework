// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.codahale.metrics.Timer;
import com.viadeo.kasper.ddd.AggregateRoot;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.*;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class MetricsRepositoryFacade<AGR extends AggregateRoot> extends RepositoryFacade<AGR> {

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
            GLOBAL_REPOSITORY_METER_SAVE_ERRORS.mark();
            getMetricRegistry().meter(meterSaveErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            GLOBAL_REPOSITORY_HISTO_SAVE_TIMES.update(time);
            getMetricRegistry().histogram(histoSavesTimesName).update(time);

            GLOBAL_REPOSITORY_METER_SAVES.mark();
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
            GLOBAL_REPOSITORY_METER_LOAD_ERRORS.mark();
            getMetricRegistry().meter(meterLoadErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            GLOBAL_REPOSITORY_HISTO_LOAD_TIMES.update(time);
            getMetricRegistry().histogram(histoLoadsTimesName).update(time);

            GLOBAL_REPOSITORY_METER_LOADS.mark();
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
            GLOBAL_REPOSITORY_METER_DELETE_ERRORS.mark();
            getMetricRegistry().meter(meterDeleteErrorsName).mark();
            throw e;

        } finally {
            final long time = timer.stop();
            GLOBAL_REPOSITORY_HISTO_DELETE_TIMES.update(time);
            getMetricRegistry().histogram(histoDeletesTimesName).update(time);

            GLOBAL_REPOSITORY_METER_DELETES.mark();
            getMetricRegistry().meter(meterDeletesName).mark();
        }

    }

}
