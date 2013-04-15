// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IEntity;
import com.viadeo.kasper.ddd.IInternalDomain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.locators.IDomainLocator;

/**
 *
 * Base Kasper domain implementation
 *
 * @param <D> Domain
 * 
 * @see IDomain
 */
public abstract class AbstractDomain implements IInternalDomain {
	private IDomainLocator domainLocator;

	// ------------------------------------------------------------------------

	@Override
	public String getPrefix() {
		return this.domainLocator.getDomainPrefix(this);
	}

	@Override
	public String getName() {
		return this.domainLocator.getDomainName(this);
	}

	// ------------------------------------------------------------------------

	public void setDomainLocator(final IDomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}

	public IDomainLocator getDomainLocator() {
		return this.domainLocator;
	}

	// ------------------------------------------------------------------------

	@Override
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(final E entity) {
		return this.domainLocator.getEntityRepository(Preconditions.checkNotNull(entity));
	}

	@Override
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(final Class<E> entityClass) {
		return this.domainLocator.getEntityRepository(Preconditions.checkNotNull(entityClass));
	}

	// ------------------------------------------------------------------------

	@Override
	public Set<? extends IEntity> getDomainEntities() {
		return (Set<? extends IEntity>) this.domainLocator.getDomainEntities(this.getClass());
	}

}
