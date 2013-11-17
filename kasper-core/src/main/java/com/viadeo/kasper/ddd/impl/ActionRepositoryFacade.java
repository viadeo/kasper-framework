// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.event.Event;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class ActionRepositoryFacade<AGR extends AggregateRoot> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRepositoryFacade.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();

    private final Repository<AGR> kasperRepository; /* The repository to proxy actions on */

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

    private final ConcurrentMap<KasperID, DateTime> loadedModificationTimes; /* Used to track to loaded modification date */

    // ------------------------------------------------------------------------

    ActionRepositoryFacade(final Repository<AGR> kasperRepository) {
        this.kasperRepositoryClass = checkNotNull(kasperRepository).getClass();
        this.kasperRepository = kasperRepository;

        loadedModificationTimes = Maps.newConcurrentMap();
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

        /* Ensure dates are correctly set */
        this.ensureDates(aggregate);

        try {

            /**
             * Mark events persistency type
             */
            if (aggregate.getUncommittedEventCount() > 0) {
                final DomainEventStream eventStream = aggregate.getUncommittedEvents();
                while (eventStream.hasNext()) {
                    final DomainEventMessage message = eventStream.next();
                    if (Event.class.equals(message.getPayloadType())) {
                        final Event event = (Event) message.getPayload();

                        if (EventSourcedRepository.class.isAssignableFrom(this.kasperRepository.getClass())) {
                            event.setPersistencyType(Event.PersistencyType.EVENT_SOURCE);
                        } else {
                            event.setPersistencyType(Event.PersistencyType.EVENT_INFO);
                        }
                    }
                }
            }

            /**
             * Manage with save/update differentiation for Kasper repositories
             */
            if (null == aggregate.getVersion()) {
                this.kasperRepository.doSave(aggregate);
            } else {
                this.kasperRepository.doUpdate(aggregate);
            }

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

            agr = this.kasperRepository.doLoad(aggregateIdentifier, expectedVersion);

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

        /* Record the modification date during load */
        if (null != agr.getModificationDate()) {
            this.loadedModificationTimes.put(agr.getEntityId(), agr.getModificationDate());
        }

        return agr;
    }

    // ------------------------------------------------------------------------

    protected void doDelete(final AGR aggregate) {
        initMetrics();

        final Timer.Context timer = metricTimerDelete.time();

        /* Ensure dates are correctly set */
        this.ensureDates(aggregate);

        try {

            this.kasperRepository.doDelete(aggregate);

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

    // ------------------------------------------------------------------------

    /**
     * Ensure aggregate dates are correctly set before saving / deleting
     *
     * @param aggregate the aggregate to check for correct dates
     */
    private void ensureDates(final AGR aggregate) {
        if (AbstractAggregateRoot.class.isAssignableFrom(aggregate.getClass())) {
            final AbstractAggregateRoot agr = (AbstractAggregateRoot) aggregate;
            final DateTime now = DateTime.now();

            if (null == agr.getCreationDate()) { /* aggregate seems to be under creation */

                if (null != agr.getVersion()) {
                    LOGGER.warn(
                            "The aggregate {} with id {} had not a creation date while it's not a new aggregate",
                            agr.getClass().getSimpleName(),
                            agr.getEntityId()
                    );
                }
                agr.setCreationDate(now);
                agr.setModificationDate(now);

            } else if (null == agr.getModificationDate()) { /* aggregate seems to be under modification */

                if (null == agr.getVersion()) { /* it's a new aggregate */
                    agr.setModificationDate(agr.getCreationDate());
                } else {
                    agr.setModificationDate(now);
                }
            }

            /* The modification date has not been changed since loading */
            if (this.loadedModificationTimes.containsKey(agr.getEntityId())) {
                final DateTime loadedModificationTime = this.loadedModificationTimes.get(agr.getEntityId());
                if (agr.getModificationDate().equals(loadedModificationTime)) {
                    agr.setModificationDate(now);
                }
                this.loadedModificationTimes.remove(agr.getEntityId(), loadedModificationTime);
            }
        }
    }

}
