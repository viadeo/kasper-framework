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
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.repository.AggregateNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * 
 * Base Kasper repository implementation
 * 
 * Axon repository decorator
 * 
 * Axon Repository needs entity class type at construction time
 * So we store an implementation of Axon Repository, binding its abstract methods to ours
 * and sending Repository interface calls to it
 *
 * @param <AGR> Aggregate Root
 */
public abstract class Repository<AGR extends AggregateRoot> implements IRepository<AGR> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();
	
	/**
	 * Decored axon repository
	 * 
	 * 1- Manages with Axon.Repository interface calls
	 * 2- Delegates to (this) the Axon.AbstractRepository abstract methods execution
	 * 
	 */
	private class AxonRepository extends org.axonframework.repository.AbstractRepository<AGR> {

		private final Repository<AGR> kasperRepository;

        private final Class kasperRepositoryClass;

        private final Histogram metricClassSaveTimes = METRICS.histogram(name(IRepository.class, "save-times"));
        private final Meter metricClassSaves = METRICS.meter(name(IRepository.class, "saves"));
        private final Meter metricClassSaveErrors = METRICS.meter(name(IRepository.class, "save-errors"));

        private final Histogram metricClassLoadTimes = METRICS.histogram(name(IRepository.class, "load-times"));
        private final Meter metricClassLoads = METRICS.meter(name(IRepository.class, "loads"));
        private final Meter metricClassLoadErrors = METRICS.meter(name(IRepository.class, "load-errors"));

        private final Histogram metricClassDeleteTimes = METRICS.histogram(name(IRepository.class, "delete-times"));
        private final Meter metricClassDeletes= METRICS.meter(name(IRepository.class, "deletes"));
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

        private final ConcurrentMap<AggregateRoot, DateTime> loadedModificationTimes; /* Used to track to loaded modification date */

        // --------------------------------------------------------------------
		
		protected AxonRepository(final Repository<AGR> kasperRepository, final Class<AGR> aggregateType) {
			super(aggregateType);

            this.kasperRepositoryClass = kasperRepository.getClass();
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

        // --------------------------------------------------------------------

		@Override
		protected void doSave(final AGR aggregate) {
            initMetrics();;

            final Timer.Context timer = metricTimerSave.time();

            /* Ensure dates are correctly set */
            this.ensureDates(aggregate);

            try {

			    this.kasperRepository.doSave(aggregate);

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

		@Override
		protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
            initMetrics();;

            final Timer.Context timer = metricTimerLoad.time();

            final AGR agr;
            try {
                agr = this.kasperRepository.doLoad(aggregateIdentifier, expectedVersion);

                if (agr.isDeleted()) {
                    throw new AggregateNotFoundException(agr.getEntityId(), "Not found");
                }

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
                this.loadedModificationTimes.put(agr, agr.getModificationDate());
            }

            return agr;
		}

		@Override
		protected void doDelete(final AGR aggregate) {
            initMetrics();;

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

        // --------------------------------------------------------------------

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
                if (this.loadedModificationTimes.containsKey(agr)) {
                    final DateTime loadedModificationTime = this.loadedModificationTimes.get(agr);
                    if (agr.getModificationDate().equals(loadedModificationTime)) {
                        agr.setModificationDate(now);
                    }
                    this.loadedModificationTimes.remove(agr, loadedModificationTime);
                }
            }
        }
		
	}
	
	private AxonRepository axonRepository; 
	
	// ========================================================================
	
	public Repository() { }

	@Override
	public void init() {
		
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<AGR>> entityType =
				(Optional<Class<AGR>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), IRepository.class, IRepository.ENTITY_PARAMETER_POSITION));
		
		if (!entityType.isPresent()) {
			throw new KasperException("Cannot determine entity type for " + this.getClass().getName());
		}
		
		axonRepository = new AxonRepository(this, entityType.get());
	}
	
	// ------------------------------------------------------------------------
	
	public void setEventBus(final EventBus eventBus) {
		this.axonRepository.setEventBus(Preconditions.checkNotNull(eventBus));
	}
	
	// ========================================================================	
	// Redirect Axon.Repository calls to decored instance
	// ========================================================================
	
	/**
	 * @see org.axonframework.repository.Repository#load(java.lang.Object, java.lang.Long)
	 */
	@Override
	public AGR load(final Object aggregateIdentifier, final Long expectedVersion) {
		return this.axonRepository.load(aggregateIdentifier, expectedVersion);
	}

	/**
	 * @see org.axonframework.repository.Repository#load(java.lang.Object)
	 */
	@Override
	public AGR load(final Object aggregateIdentifier) {
		return this.axonRepository.load(aggregateIdentifier);
	}

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * @param aggregateIdentifier the aggregate identifier to fetch
     * @param expectedVersion the aggregate expected version to fetch
     * @return the fetched aggregate if any
     */
    @Override
    public AGR get(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return this.doLoad((Object) aggregateIdentifier, expectedVersion);
    }

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * @param aggregateIdentifier
     * @return the fetched aggregate if any
     */
    @Override
    public AGR get(final KasperID aggregateIdentifier) {
        return this.get(aggregateIdentifier, null);
    }

	/**
	 * @see org.axonframework.repository.Repository#add(Object)
	 */
	@Override
	public void add(final AGR aggregate) {
        /* All aggregates must have an ID */
        if (null == aggregate.getIdentifier()) {
            throw new KasperCommandException("Aggregates must have an ID (use setID()) before saves");
        }

		this.axonRepository.add(aggregate);
	}

    /**
     * (Optional) indicates existence of an aggregate in the repository
     *
     * @param id the aggregate id
     * @return true if this aggregate exists
     */
    @Override
    public boolean has(final KasperID id) {
        return this.doHas(id);
    }

	// ========================================================================
	
	/**
	 * Load an aggregate from the repository
	 * 
	 * Convenient Axon wrapper for proper Kasper typing and ID type conformance checking
	 * 
	 * @param aggregateIdentifier the aggregate identifier
	 * @param expectedVersion the version of the aggregate to load
	 * 
	 * @return the aggregate
	 */
	protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
		Preconditions.checkNotNull(aggregateIdentifier);
		
		if (KasperID.class.isAssignableFrom(aggregateIdentifier.getClass())) {
			
			final Optional<AGR> agr = this.doLoad((KasperID) aggregateIdentifier, expectedVersion);
			if (agr.isPresent()) {
				return agr.get();
			}
			
			throw new AggregateNotFoundException(aggregateIdentifier, "Not found aggregate"); // Axon
			
		} else {
			throw new KasperException("Unable to manage with identifier of this kind : " + aggregateIdentifier.getClass());
		}
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * loads an aggregate from the repository
	 * 
	 * @param aggregateIdentifier the aggregate identifier
	 * @param expectedVersion the version of the aggregate to load
	 * 
	 * @return the (optional) aggregate
	 */
	protected abstract Optional<AGR> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion);
	
	/**
	 * saves a new (create) or existing (update) aggregate to the repository
	 * 
	 * @param aggregate the aggregate to be saved on the repository
	 */
	protected abstract void doSave(final AGR aggregate);
	
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
    protected boolean doHas(final KasperID aggregateIdentifier) {
        throw new UnsupportedOperationException("has() operation not implemented");
    }

}
