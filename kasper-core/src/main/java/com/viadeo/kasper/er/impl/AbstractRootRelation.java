// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.ddd.impl.AbstractAggregateRoot;
import com.viadeo.kasper.er.IRootConcept;
import com.viadeo.kasper.er.IRootRelation;

/**
 *
 * Base Kasper Relation Aggregate Root implementation
 *
 * @param <S> Source concept of the relation
 * @param <T> Target concept of the relation
 * 
 * @see com.viadeo.kasper.er.IRelation
 * @see IRootRelation
 * @see com.viadeo.kasper.ddd.IAggregateRoot
 */
public abstract class AbstractRootRelation<S extends IRootConcept, T extends IRootConcept> 
		extends AbstractAggregateRoot
		implements IRootRelation<S, T> {

	private static final long serialVersionUID = 4719442806097449770L;

	private IKasperID sourceId;
	private IKasperID targetId;

	// ------------------------------------------------------------------------

	protected void setId(final IKasperID id, final IKasperID sourceId, final IKasperID targetId) {
		super.setId(id);

		this.sourceId = Preconditions.checkNotNull(sourceId);
		this.targetId = Preconditions.checkNotNull(targetId);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.er.IRelation#getSourceIdentifier()
	 */
	@Override
	public IKasperID getSourceIdentifier() {
		return this.sourceId;
	}

	/**
	 * @see com.viadeo.kasper.er.IRelation#getTargetIdentifier()
	 */
	@Override
	public IKasperID getTargetIdentifier() {
		return this.targetId;
	}

	/**
	 * @see com.viadeo.kasper.er.IRelation#isBidirectional()
	 */
	@Override
	public boolean isBidirectional() {
		// TODO
		return false;
	}

}
