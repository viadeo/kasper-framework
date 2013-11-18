// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class MetricsRepositoryFacade<AGR extends AggregateRoot> extends RepositoryFacade<AGR> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsRepositoryFacade.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private final Class kasperRepositoryClass;

    private final Histogram metricClassSaveTimes = METRICS.histogram(name(IRepository.class, "save-times"));
    private final Meter metricClassSaves = METRICS.meter(name(IRepository.class, "saves"));
    private final Meter metricClassSaveErrors = METRICS.meter(name(IRepository.class, "save-errors"));

    private final Histogram metricClassLoadTimes = METRICS.histogram(name(IRepository.class, "load-times"));
    private final Meter metricClassLoads = METRICS.meter(name(IRepository.class, "loads"));
    private final Meter metricClassLoadErrors = METRICS.meter(name(IRepository.class, "load-errors"));

    private final Histogram metricClassDeleteTimes = METRICS.histogram(name(IRepository.class, "delete-times"));
    private final Meter metricClassDeletes = METRICS.meter(name(IRepository.class, "deletes"));
    private final Meter metricClassDeleteErrors = METRICS.meter(name(IRepository.class, "delete-errors"));

    private Timer metricTimerSave;
    private Histogram metricSaveTimes;
    private Meter metricSaves;
    private Meter metricSaveErrors;

    private Timer metricTimerLoad;
    private Histogram metricLoadTimes;
    private Meter metricLoads;
    private Meter metricLoadErrors;

    private Timer metricTimerDelete;
    private Histogram metricDeleteTimes;
    private Meter metricDeletes;
    private Meter metricDeleteErrors;

    // ------------------------------------------------------------------------

    MetricsRepositoryFacade(final Repository<AGR> kasperRepository) {
        super(kasperRepository);
        this.kasperRepositoryClass = checkNotNull(kasperRepository).getClass();
    }

    private final void initMetrics() {
        if (null == metricTimerSave) {
            metricTimerSave = METRICS.timer(name(kasperRepositoryClass, "save-time"));
            metricSaveTimes = METRICS.histogram(name(kasperRepositoryClass, "save-times"));
            metricSaves = METRICS.meter(name(kasperRepositoryClass, "saves"));
            metricSaveErrors = METRICS.meter(name(kasperRepositoryClass, "save-errors"));

            metricTimerLoad = METRICS.timer(name(kasperRepositoryClass, "load-time"));
            metricLoadTimes = METRICS.histogram(name(kasperRepositoryClass, "load-times"));
            metricLoads = METRICS.meter(name(kasperRepositoryClass, "loads"));
            metricLoadErrors = METRICS.meter(name(kasperRepositoryClass, "load-errors"));

            metricTimerDelete = METRICS.timer(name(kasperRepositoryClass, "delete-time"));
            metricDeleteTimes = METRICS.histogram(name(kasperRepositoryClass, "delete-times"));
            metricDeletes = METRICS.meter(name(kasperRepositoryClass, "deletes"));
            metricDeleteErrors = METRICS.meter(name(kasperRepositoryClass, "delete-errors"));
        }
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {
        initMetrics();

        final Timer.Context timer = metricTimerSave.time();

        try {

            super.doSave(aggregate);

        } catch (final RuntimeException e) {
            metricClassSaveErrors.mark();
            metricSaveErrors.mark();
            throw e;

        } finally {
            final long time = timer.stop();
            metricSaveTimes.update(time);
            metricClassSaveTimes.update(time);
            metricSaves.mark();
            metricClassSaves.mark();
        }

    }

    // ------------------------------------------------------------------------

    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        initMetrics();

        final Timer.Context timer = metricTimerLoad.time();

        final AGR agr;
        try {

            agr = super.doLoad(aggregateIdentifier, expectedVersion);

        } catch (final RuntimeException e) {
            metricClassLoadErrors.mark();
            metricLoadErrors.mark();
            throw e;

        } finally {
            final long time = timer.stop();
            metricLoadTimes.update(time);
            metricClassLoadTimes.update(time);
            metricLoads.mark();
            metricClassLoads.mark();
        }

        return agr;
    }

    // ------------------------------------------------------------------------

    protected void doDelete(final AGR aggregate) {
        initMetrics();

        final Timer.Context timer = metricTimerDelete.time();

        try {

            super.doDelete(aggregate);

        } catch (final RuntimeException e) {
            metricClassDeleteErrors.mark();
            metricDeleteErrors.mark();
            throw e;

        } finally {
            final long time = timer.stop();
            metricDeleteTimes.update(time);
            metricClassDeleteTimes.update(time);
            metricDeletes.mark();
            metricClassDeletes.mark();
        }

    }

}
