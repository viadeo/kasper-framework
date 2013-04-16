// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import org.joda.time.DateTime;

import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.event.domain.er.IConceptEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;

/**
 *
 * Base implementation for Kasper event on Concept
 * 
 * @see AbstractEntityEvent
 * @see IConceptEvent
 * @see IEvent
 */
public abstract class AbstractConceptEvent 
		extends AbstractEntityEvent 
		implements IConceptEvent {

	private static final long serialVersionUID = 578218386414763706L;

	// ------------------------------------------------------------------------
	
    protected AbstractConceptEvent() { /* For serialization */ }

	protected AbstractConceptEvent(final IKasperID id, final DateTime lastModificationDate) {
		super(id, lastModificationDate);
	}

}
