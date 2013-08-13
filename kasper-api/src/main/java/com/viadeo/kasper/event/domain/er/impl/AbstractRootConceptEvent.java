// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.er.RootConceptEvent;
import com.viadeo.kasper.event.domain.impl.AbstractRootEntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Concept
 * 
 * @see com.viadeo.kasper.event.domain.impl.AbstractDomainEvent
 * @see com.viadeo.kasper.event.domain.er.RootConceptEvent
 */
public abstract class AbstractRootConceptEvent<D extends Domain, C>
		extends AbstractRootEntityEvent<D, C>
		implements RootConceptEvent<D,C> {

	private static final long serialVersionUID = 578218386414763706L;

	// ------------------------------------------------------------------------
	
    protected AbstractRootConceptEvent() {
        /* For serialization */
        super();
    }

	protected AbstractRootConceptEvent(final Context context,
                                       final KasperID id,
                                       final DateTime lastModificationDate) {
		super(context, id, lastModificationDate);
	}

}
