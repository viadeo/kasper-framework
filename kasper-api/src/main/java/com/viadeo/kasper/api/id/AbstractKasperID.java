// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple IKasperID parameterizable implementation
 *
 * @param <T> the internal IKasperID type
 */
public abstract class AbstractKasperID<T extends Serializable> implements KasperID {
	private static final long serialVersionUID = 5118678966818650797L;

	private T id;
	
	// ------------------------------------------------------------------------

    protected AbstractKasperID() {

    }

	protected AbstractKasperID(final T id) {
		this.id = checkNotNull(id);
	}	
	
	protected void setId(final T id) {
		this.id = checkNotNull(id);
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public boolean equals(final Object otherId) {
		if (this == checkNotNull(otherId)) {
			return true;
		}
		if (KasperID.class.isAssignableFrom(otherId.getClass())) {
			final KasperID other = (KasperID) otherId;
			return this.toString().equals(other.toString());
		}		
		return this.id.equals(otherId);
	}	
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public T getId() {
		return this.id;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return this.id.toString();
	}

}
