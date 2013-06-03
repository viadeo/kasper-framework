// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.event.domain.er.IRelationEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Relation
 *
 * @see AbstractEntityEvent
 * @see IRelationEvent
 */
public abstract class AbstractRelationEvent 
		extends AbstractEntityEvent 
		implements IRelationEvent {

	private static final long serialVersionUID = 2649090309164938753L;

	private final IKasperID sourceId;
	private final IKasperID targetId;

	// ------------------------------------------------------------------------

	protected AbstractRelationEvent(final IKasperID id, final IKasperID sourceId, final IKasperID targetId, final DateTime lastModificationDate) {
		super(id, lastModificationDate);

		this.sourceId = Preconditions.checkNotNull(sourceId);
		this.targetId = Preconditions.checkNotNull(targetId);
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
