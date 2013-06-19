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
import com.viadeo.kasper.ddd.ComponentEntity;
import com.viadeo.kasper.er.RootConcept;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Base Component entity implementation
 *
 * @param <R> Root Concept
 * 
 * @see com.viadeo.kasper.ddd.ComponentEntity
 * @see com.viadeo.kasper.Domain
 */
public abstract class AbstractComponentEntity<R extends RootConcept>
		extends AbstractAnnotatedEntity 
		implements ComponentEntity<R> {
	
	private static final long serialVersionUID = -5072753617155152220L;

	// TODO: override AbstractAnnotatedEntity.getChildEntities() from the domainLocator 
	//        in order to improve performance as in its normal behaviour Axon will apply 
	//        reflection for all instances
	
	// =======================================================================
	
	@Autowired
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
