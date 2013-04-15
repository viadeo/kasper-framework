// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.domain.er.IRelationEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;

/**
 *
 * Base implementation for Kasper event on Relation
 *
 * @see AbstractEntityEvent
 * @see IRelationEvent
 * @see IEvent
 */
public abstract class AbstractRelationEvent 
		extends AbstractEntityEvent 
		implements IRelationEvent {

	private static final long serialVersionUID = 2649090309164938753L;

	private final IKasperID sourceId;
	private final IKasperID targetId;

	// ------------------------------------------------------------------------

	protected AbstractRelationEvent(final IKasperID id, final IKasperID source_id, final IKasperID target_id, final DateTime lastModificationDate) {
		super(id, lastModificationDate);

		this.sourceId = Preconditions.checkNotNull(source_id);
		this.targetId = Preconditions.checkNotNull(target_id);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.domain.er.IRelationEvent#getSourceId()
	 */
	@Override
	public IKasperID getSourceId() {
		return this.sourceId;
	}

	/**
	 * @see com.viadeo.kasper.event.domain.er.IRelationEvent#getTargetId()
	 */
	@Override
	public IKasperID getTargetId() {
		return this.targetId;
	}

}
