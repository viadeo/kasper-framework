// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.repository.AggregateNotFoundException;
import org.axonframework.repository.Repository;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.exception.KasperDomainRepositoryRuntimeException;
import com.viadeo.kasper.ddd.exception.KasperDomainRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

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
 * @param <D> Domain
 * @param <AGR> Aggregate Root
 */
public abstract class AbstractRepository<AGR extends IAggregateRoot> 
		implements Repository<AGR>, IRepository<AGR> {
	
	/**
	 * Decored axon repository
	 * 
	 * 1- Manages with Axon.Repository interface calls
	 * 2- Delegates to (this) the Axon.AbstractRepository abstract methods execution
	 * 
	 */
	private class AxonRepository extends org.axonframework.repository.AbstractRepository<AGR> {

		private final AbstractRepository<AGR> kasperRepository;
		
		protected AxonRepository(final AbstractRepository<AGR> kasperRepository, final Class<AGR> aggregateType) {
			super(aggregateType);
			this.kasperRepository = kasperRepository;
		}

		@Override
		protected void doSave(final AGR aggregate) {
			this.kasperRepository.doSave(aggregate);
		}

		@Override
		protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
			return this.kasperRepository.doLoad(aggregateIdentifier, expectedVersion);
		}

		@Override
		protected void doDelete(final AGR aggregate) {
			this.kasperRepository.doDelete(aggregate);			
		}
		
	};
	
	private AxonRepository axonRepository; 
	
	// ========================================================================
	
	public AbstractRepository() {
	}

	@Override
	public void init() {
		
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<AGR>> entityType =
				(Optional<Class<AGR>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), IRepository.class, IRepository.ENTITY_PARAMETER_POSITION));
		
		if (!entityType.isPresent()) {
			throw new KasperDomainRuntimeException("Cannot determine entity type for " + this.getClass().getName());
		}
		
		axonRepository = new AxonRepository(this, entityType.get());
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.ddd.IRepository#setEventBus(org.axonframework.eventhandling.EventBus)
	 */
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
	 * @see org.axonframework.repository.Repository#add(org.axonframework.domain.AggregateRoot)
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
		
		if (IKasperID.class.isAssignableFrom(aggregateIdentifier.getClass())) {
			
			final Optional<AGR> agr = this.doLoad((IKasperID) aggregateIdentifier, expectedVersion);
			if (agr.isPresent()) {
				return agr.get();
			}
			
			throw new AggregateNotFoundException(aggregateIdentifier, "Not found aggregate"); // Axon
			
		} else {
			throw new KasperDomainRepositoryRuntimeException("Unable to manage with identifier of this kind : " + aggregateIdentifier.getClass());
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
	protected abstract Optional<AGR> doLoad(final IKasperID aggregateIdentifier, final Long expectedVersion);
	
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
