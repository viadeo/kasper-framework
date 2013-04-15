// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.domain.IEntityEvent;
import com.viadeo.kasper.event.impl.AbstractEvent;

/**
 *
 * Base implementation for entity events
 *
 * @see IEntityEvent
 * @see IEvent
 */
public abstract class AbstractEntityEvent extends AbstractEvent implements IEntityEvent {

	private static final long serialVersionUID = -1948165707419476512L;

	private IKasperID entityId;
	private DateTime lastEntityModificationDate;

	// ------------------------------------------------------------------------
    protected AbstractEntityEvent() { /* For serialization */ }

	protected AbstractEntityEvent(final IKasperID id, final DateTime lastModificationDate) {
		this.entityId = Preconditions.checkNotNull(id);
		this.lastEntityModificationDate = Preconditions.checkNotNull(lastModificationDate);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.domain.IEntityEvent#getEntityId()
	 */
	@Override
	public IKasperID getEntityId() {
		return this.entityId;
	}

	// ------------------------------------------------------------------------
	
	public DateTime getEntityLastModificationDate() {
		return this.lastEntityModificationDate;
	}
	
}
