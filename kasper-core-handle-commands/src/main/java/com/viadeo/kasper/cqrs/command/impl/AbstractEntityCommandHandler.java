// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.cqrs.command.IEntityCommandHandler;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandRuntimeException;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IEntity;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.event.exceptions.KasperEventRuntimeException;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 * Base implementation for Kasper entity command handlers
 *
 * @param <C> Command
 * @param <AGR> the entity (aggregate root)
 * 
 * @see IAggregateRoot
 * @see IEntityCommandHandler
 * @see ICommandHandler
 * @see IEntity
 * @see IAggregateRoot
 */
public abstract class AbstractEntityCommandHandler<C extends ICommand, AGR extends IAggregateRoot> 
		extends AbstractCommandHandler<C> 
		implements IEntityCommandHandler<C, AGR> {

	private IDomainLocator domainLocator;
	
	// Consistent data container for entity class and repository
	private static final class Consistent<E extends IAggregateRoot> {
		IRepository<E> repository;
		Class<E> entityClass;
		
		@SuppressWarnings("unchecked")
		void setEntityClass(final Class<?> entityClass) {
			this.entityClass = (Class<E>) entityClass;
		}
		
		@SuppressWarnings("unchecked")
		void setRepository(final IRepository<?> repository) {
			this.repository = (IRepository<E>) repository;
		}
	}
	@SuppressWarnings("rawtypes")
	private final Consistent<?> consistent = new Consistent();
	
	// ------------------------------------------------------------------------

	public AbstractEntityCommandHandler() {
		super();
		
		//- Extract entity class for further repository lookup ----------------
		// TODO: to check if performance optimization is needed (ConcurrentMap cache)
		
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<? extends IAggregateRoot>> entityAssignClass = 
			(Optional<Class<? extends IAggregateRoot>>) 
				ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), IEntityCommandHandler.class, IEntityCommandHandler.ENTITY_PARAMETER_POSITION);

		if (!entityAssignClass.isPresent()) {
			throw new KasperEventRuntimeException("Cannot determine entity type for " + this.getClass().getName());
		}

		this.consistent.setEntityClass(entityAssignClass.get());
	}

	// ------------------------------------------------------------------------

	/**
	 * @param domainLocator
	 */
	public void setDomainLocator(final IDomainLocator domainLocator) {
		this.domainLocator = domainLocator;
	}

	// ========================================================================
	
	/**
	 * @see com.viadeo.kasper.cqrs.command.IEntityCommandHandler#setRepository(com.viadeo.kasper.ddd.IRepository)
	 */
	@Override
	public void setRepository(final IRepository<AGR> repository) {
		this.consistent.setRepository(Preconditions.checkNotNull(repository));
	}

	/**
	 * @see com.viadeo.kasper.cqrs.command.IEntityCommandHandler#getRepository()
	 */
	@Override
	public <R extends IRepository<AGR>> R getRepository() {
		if (null == this.consistent.repository) {
			if (null == this.domainLocator) {
				throw new KasperCommandRuntimeException("Unable to resolve repository, no domain locator was provided");
			}
			this.consistent.setRepository(this.domainLocator.getEntityRepository(this.consistent.entityClass));
		}
		@SuppressWarnings("unchecked") // To be ensured by client
		final R repo = (R) this.consistent.repository;
		return repo;
	}

}
