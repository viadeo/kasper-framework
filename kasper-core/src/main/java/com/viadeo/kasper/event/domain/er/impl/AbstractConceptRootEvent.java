// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.event.domain.er.ConceptRootEvent;
import com.viadeo.kasper.event.domain.impl.AbstractDomainEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Concept
 * 
 * @see com.viadeo.kasper.event.domain.impl.AbstractDomainEvent
 * @see com.viadeo.kasper.event.domain.er.ConceptRootEvent
 */
public abstract class AbstractConceptRootEvent<D extends Domain, C extends RootConcept>
		extends AbstractDomainEvent<D, C>
		implements ConceptRootEvent<D,C> {

	private static final long serialVersionUID = 578218386414763706L;

	// ------------------------------------------------------------------------
	
    protected AbstractConceptRootEvent() { /* For serialization */ }

	protected AbstractConceptRootEvent(final KasperID id, final DateTime lastModificationDate) {
		super(id, lastModificationDate);
	}

}
