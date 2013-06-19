// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.domain.er.RelationEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Relation
 *
 * @see AbstractEntityEvent
 * @see com.viadeo.kasper.event.domain.er.RelationEvent
 */
public abstract class AbstractRelationEvent 
		extends AbstractEntityEvent 
		implements RelationEvent {

	private static final long serialVersionUID = 2649090309164938753L;

	private final KasperID sourceId;
	private final KasperID targetId;

	// ------------------------------------------------------------------------

	protected AbstractRelationEvent(final KasperID id, final KasperID sourceId, final KasperID targetId, final DateTime lastModificationDate) {
		super(id, lastModificationDate);

		this.sourceId = Preconditions.checkNotNull(sourceId);
		this.targetId = Preconditions.checkNotNull(targetId);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.domain.er.RelationEvent#getSourceId()
	 */
	@Override
	public KasperID getSourceId() {
		return this.sourceId;
	}

	/**
	 * @see com.viadeo.kasper.event.domain.er.RelationEvent#getTargetId()
	 */
	@Override
	public KasperID getTargetId() {
		return this.targetId;
	}

}
