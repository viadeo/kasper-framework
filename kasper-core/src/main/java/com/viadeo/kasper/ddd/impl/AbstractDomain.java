// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.InternalDomain;
import com.viadeo.kasper.ddd.Repository;

import java.util.Set;

/**
 *
 * Base Kasper domain implementation
 *
 * @see com.viadeo.kasper.ddd.InternalDomain
 */
public abstract class AbstractDomain implements InternalDomain {
	private DomainLocator domainLocator;

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

	public void setDomainLocator(final DomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}

	public DomainLocator getDomainLocator() {
		return this.domainLocator;
	}

	// ------------------------------------------------------------------------

	@Override
	public <E extends AggregateRoot> Repository<E> getEntityRepository(final E entity) {
		return this.domainLocator.getEntityRepository(Preconditions.checkNotNull(entity));
	}

	@Override
	public <E extends AggregateRoot> Repository<E> getEntityRepository(final Class<E> entityClass) {
		return this.domainLocator.getEntityRepository(Preconditions.checkNotNull(entityClass));
	}

	// ------------------------------------------------------------------------

	@Override
	public Set<? extends Entity> getDomainEntities() {
		return this.domainLocator.getDomainEntities(this.getClass());
	}

}
