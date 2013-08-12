// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.impl.AbstractAggregateRoot;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.RootRelation;

/**
 *
 * Base Kasper Relation Aggregate Root implementation
 *
 * @param <S> Source concept of the relation
 * @param <T> Target concept of the relation
 * 
 * @see com.viadeo.kasper.er.Relation
 * @see com.viadeo.kasper.er.RootRelation
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public abstract class AbstractRootRelation<S extends RootConcept, T extends RootConcept>
		extends AbstractAggregateRoot
		implements RootRelation<S, T> {

	private static final long serialVersionUID = 4719442806097449770L;

	private KasperID sourceId;
	private KasperID targetId;

	// ------------------------------------------------------------------------

	protected void setId(final KasperID id, final KasperID sourceId, final KasperID targetId) {
		super.setId(id);

		this.targetId = Preconditions.checkNotNull(targetId);
 		this.sourceId = Preconditions.checkNotNull(sourceId);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.er.Relation#getSourceIdentifier()
	 */
	@Override
	public KasperID getSourceIdentifier() {
		return this.sourceId;
	}

	/**
	 * @see com.viadeo.kasper.er.Relation#getTargetIdentifier()
	 */
	@Override
	public KasperID getTargetIdentifier() {
		return this.targetId;
	}

	/**
	 * @see com.viadeo.kasper.er.Relation#isBidirectional()
	 */
	@Override
	public boolean isBidirectional() {
		// TODO
		return false;
	}

}
