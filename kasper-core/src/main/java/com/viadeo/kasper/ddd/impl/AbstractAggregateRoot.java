// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.Domain;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Repository;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Base AGR implementation
 *
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see com.viadeo.kasper.Domain
 */
public abstract class AbstractAggregateRoot 
		extends AbstractAnnotatedAggregateRoot<KasperID>
		implements AggregateRoot {
	
	private static final long serialVersionUID = 8352516744342839116L;
	
	@Autowired // FIXME: remove autowiring ??
	private transient DomainLocator domainLocator;
	
	@AggregateIdentifier
	private KasperID id;
	
	private DateTime creationDate;
	
	private DateTime modificationDate;	
	
	// ========================================================================
	
	protected void setId(final KasperID id) {
		this.id = id;
	}
	
	// ========================================================================

	public <E extends AggregateRoot> Repository<E> getRepository() {
        return (Repository<E>)
            this.getDomainLocator().getEntityRepository(this.getClass());
	}
	
	// ========================================================================	
	
	@SuppressWarnings("unchecked")
	@Override
	public <I extends KasperID> I  getEntityId() {
		return (I) this.id;
	}
	
	@Override
	public Domain getDomain() {
		return domainLocator.getEntityDomain(this);
	}

	// ========================================================================
	
	@Override
	public void setDomainLocator(final DomainLocator domainLocator) {
		this.domainLocator = Preconditions.checkNotNull(domainLocator);
	}
	
	public DomainLocator getDomainLocator() {
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
