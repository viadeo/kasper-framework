// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.er.RelationRootEvent;
import com.viadeo.kasper.event.domain.impl.AbstractRootEntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for Kasper event on Relation
 *
 * @see com.viadeo.kasper.event.domain.impl.AbstractDomainEvent
 * @see com.viadeo.kasper.event.domain.er.RelationRootEvent
 */
public abstract class AbstractRelationRootEvent<D extends Domain, R>
		extends AbstractRootEntityEvent<D, R>
		implements RelationRootEvent<D, R> {

	private static final long serialVersionUID = 2649090309164938753L;

	private final KasperID sourceId;
	private final KasperID targetId;

	// ------------------------------------------------------------------------

	protected AbstractRelationRootEvent(final Context context,
                                        final KasperID id,
                                        final KasperID sourceId,
                                        final KasperID targetId,
                                        final DateTime lastModificationDate) {
		super(context, id, lastModificationDate);

		this.sourceId = Preconditions.checkNotNull(sourceId);
		this.targetId = Preconditions.checkNotNull(targetId);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.domain.er.RelationRootEvent#getSourceId()
	 */
	@Override
	public KasperID getSourceId() {
		return this.sourceId;
	}

	/**
	 * @see com.viadeo.kasper.event.domain.er.RelationRootEvent#getTargetId()
	 */
	@Override
	public KasperID getTargetId() {
		return this.targetId;
	}

}
