// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.values.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.values.Value;

import java.io.Serializable;

/**
 *
 * A base value used to enclose a classical type, ex : PersonName extends KasperEnclosingValue<String
 *
 * @param <ANSWER> The enclosed type
 */
public abstract class AbstractEnclosingValue<ANSWER extends Serializable> 
		implements Value {

	private static final long serialVersionUID = -2912518894544854252L;

	protected final ANSWER value;
	
	// ------------------------------------------------------------------------
	
	public AbstractEnclosingValue(final ANSWER value) {
		super();
		
		this.value = Preconditions.checkNotNull(value);
	}
	
	public ANSWER getValue() {
		return value;
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object otherValue) {
		if (this == Preconditions.checkNotNull(otherValue)) {
			return true;
		}
		if (this.getClass().isInstance(otherValue)) {
			@SuppressWarnings("unchecked")
			final AbstractEnclosingValue<ANSWER> other = (AbstractEnclosingValue<ANSWER>) otherValue;
			return value.equals(other.value);
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
}
