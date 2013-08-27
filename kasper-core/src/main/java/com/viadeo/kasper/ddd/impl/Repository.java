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
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.repository.AggregateNotFoundException;

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

    private static final MetricRegistry metrics = KasperMetrics.getRegistry();
	
	/**
	 * Decored axon repository
	 * 
	 * 1- Manages with Axon.Repository interface calls
	 * 2- Delegates to (this) the Axon.AbstractRepository abstract methods execution
	 * 
	 */
	private class AxonRepository extends org.axonframework.repository.AbstractRepository<AGR> {

		private final Repository<AGR> kasperRepository;

        private final Histogram metricClassSaveTimes = metrics.histogram(name(IRepository.class, "save-times"));
        private final Meter metricClassSaves = metrics.meter(name(IRepository.class, "saves"));
        private final Histogram metricClassLoadTimes = metrics.histogram(name(IRepository.class, "load-times"));
        private final Meter metricClassLoads = metrics.meter(name(IRepository.class, "loads"));
        private final Histogram metricClassDeleteTimes = metrics.histogram(name(IRepository.class, "delete-times"));
        private final Meter metricClassDeletes= metrics.meter(name(IRepository.class, "deletes"));

        private final Timer metricTimerSave;
        private final Histogram metricSaveTimes;
        private final Meter metricSaves;

        private final Timer metricTimerLoad;
        private final Histogram metricLoadTimes;
        private final Meter metricLoads;

        private final Timer metricTimerDelete;
        private final Histogram metricDeleteTimes;
        private final Meter metricDeletes;

        // --------------------------------------------------------------------
		
		protected AxonRepository(final Repository<AGR> kasperRepository, final Class<AGR> aggregateType) {
			super(aggregateType);
			this.kasperRepository = kasperRepository;

            metricTimerSave = metrics.timer(name(kasperRepository.getClass(), "save-time"));
            metricSaveTimes = metrics.histogram(name(kasperRepository.getClass(), "save-times"));
            metricSaves = metrics.meter(name(kasperRepository.getClass(), "saves"));

            metricTimerLoad = metrics.timer(name(kasperRepository.getClass(), "load-time"));
            metricLoadTimes = metrics.histogram(name(kasperRepository.getClass(), "load-times"));
            metricLoads = metrics.meter(name(kasperRepository.getClass(), "loads"));

            metricTimerDelete = metrics.timer(name(kasperRepository.getClass(), "delete-time"));
            metricDeleteTimes = metrics.histogram(name(kasperRepository.getClass(), "delete-times"));
            metricDeletes = metrics.meter(name(kasperRepository.getClass(), "deletes"));
		}

        // --------------------------------------------------------------------

		@Override
		protected void doSave(final AGR aggregate) {
            final Timer.Context timer = metricTimerSave.time();

			this.kasperRepository.doSave(aggregate);

            final long time = timer.stop();
            metricSaveTimes.update(time);
            metricClassSaveTimes.update(time);
            metricSaves.mark();
            metricClassSaves.mark();
		}

		@Override
		protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
             final Timer.Context timer = metricTimerLoad.time();

            final AGR agr = this.kasperRepository.doLoad(aggregateIdentifier, expectedVersion);

            final long time = timer.stop();
            metricLoadTimes.update(time);
            metricClassLoadTimes.update(time);
            metricLoads.mark();
            metricClassLoads.mark();

            return agr;
		}

		@Override
		protected void doDelete(final AGR aggregate) {
            final Timer.Context timer = metricTimerDelete.time();

 			this.kasperRepository.doDelete(aggregate);

            final long time = timer.stop();
            metricDeleteTimes.update(time);
            metricClassDeleteTimes.update(time);
            metricDeletes.mark();
            metricClassDeletes.mark();
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
	 * @see org.axonframework.repository.Repository#add(Object)
	 */
	@Override
	public void add(final AGR aggregate) {
		this.axonRepository.add(aggregate);
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
	
}
