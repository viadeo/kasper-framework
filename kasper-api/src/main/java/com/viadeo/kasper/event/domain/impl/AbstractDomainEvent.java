// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.event.impl.AbstractEvent;

/**
 *
 * Base implementation for entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class AbstractDomainEvent<D extends Domain>
        extends AbstractEvent implements DomainEvent<D> {

	private static final long serialVersionUID = 1948164207419476512L;

    protected AbstractDomainEvent() {
        /* for serialization */
        super();
    }

	protected AbstractDomainEvent(final Context context) {
        super(context);
	}

}
