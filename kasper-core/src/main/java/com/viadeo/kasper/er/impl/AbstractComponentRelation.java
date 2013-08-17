// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.impl.AbstractComponentEntity;
import com.viadeo.kasper.er.ComponentRelation;
import com.viadeo.kasper.er.RootConcept;

/**
 * A base implementation for a component relation
 *
 * @param <S> Source concept of the relation
 * @param <T> Target concept of the relation
 * 
 * @see com.viadeo.kasper.er.Relation
 * @see com.viadeo.kasper.er.ComponentRelation
 */
public abstract class AbstractComponentRelation<S extends RootConcept, T extends RootConcept>
					extends AbstractComponentEntity<S> 
					implements ComponentRelation<S, T> {

	private static final long serialVersionUID = -5237849165883458840L;

	private KasperID sourceId;
	private KasperID targetId;

	// ------------------------------------------------------------------------

	public void setId(final KasperID sourceId, final KasperID targetId) {
		this.sourceId = Preconditions.checkNotNull(sourceId);
		this.targetId = Preconditions.checkNotNull(targetId);
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

	// ------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.targetId, this.sourceId);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == Preconditions.checkNotNull(obj)) {
			return true;
		}
		if (AbstractComponentRelation.class.isAssignableFrom(obj.getClass())) {
			@SuppressWarnings("unchecked") // Safe
			final AbstractComponentRelation<S,T> other = (AbstractComponentRelation<S,T>) obj;
			return ((this.sourceId.equals(other.getSourceIdentifier())) && (this.targetId.equals(other.getTargetIdentifier())));
		}
		return false;
	}
	
}
