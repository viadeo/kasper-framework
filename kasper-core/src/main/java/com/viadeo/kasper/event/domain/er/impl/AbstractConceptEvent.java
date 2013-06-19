// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.domain.er.ConceptEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Concept
 * 
 * @see AbstractEntityEvent
 * @see com.viadeo.kasper.event.domain.er.ConceptEvent
 */
public abstract class AbstractConceptEvent 
		extends AbstractEntityEvent 
		implements ConceptEvent {

	private static final long serialVersionUID = 578218386414763706L;

	// ------------------------------------------------------------------------
	
    protected AbstractConceptEvent() { /* For serialization */ }

	protected AbstractConceptEvent(final KasperID id, final DateTime lastModificationDate) {
		super(id, lastModificationDate);
	}

}
