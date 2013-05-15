// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.locators.IDomainLocator;

/**
 *
 * Base AGR implementation
 *
 * @param <D> Domain
 * 
 * @see IAggregateRoot
 * @see IDomain
 */
public abstract class AbstractAggregateRoot 
		extends AbstractAnnotatedAggregateRoot<IKasperID> 
		implements IAggregateRoot {
	
	private static final long serialVersionUID = 8352516744342839116L;
	
	@Autowired // FIXME: remove autowiring ??
	private transient IDomainLocator domainLocator;
	
	@AggregateIdentifier
	private IKasperID id;
	
	private DateTime creationDate;
	
	private DateTime modificationDate;	
	
	// ========================================================================
	
	protected void setId(final IKasperID id) {
		this.id = id;
	}
	
	// ========================================================================

	public <E extends IAggregateRoot> IRepository<E> getRepository() {
        @SuppressWarnings("unchecked") // Safe
		final IRepository<E> repo = (IRepository<E>) 
			this.getDomainLocator().getEntityRepository(this.getClass());

        return repo;
	}
	
	// ========================================================================	
	
	@SuppressWarnings("unchecked")
	@Override
	public <I extends IKasperID> I  getEntityId() {
		return (I) this.id;
	}
	
	@Override
	public IDomain getDomain() {
		return domainLocator.getEntityDomain(this);
	}

	// ========================================================================
	
	@Override
	public void setDomainLocator(final IDomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}
	
	public IDomainLocator getDomainLocator() {
		return this.domainLocator;
	}
	
	// ========================================================================
	
	public DateTime getCreationDate() {
		return this.creationDate;
	}
	
	public DateTime getModificationDate() {
		return this.modificationDate;
	}

	public void setCreationDate(final DateTime creationDate) {
		this.creationDate = Preconditions.checkNotNull(creationDate);
		this.modificationDate = creationDate;
	}
	
	public void setModificationDate(final DateTime modificationDate) {
		this.modificationDate = Preconditions.checkNotNull(modificationDate);
	}
	
}
