// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.specification.EntitySpecification;

/**
 *
 * @param <E> Entity
 * 
 * @see com.viadeo.kasper.ddd.specification.EntitySpecification
 * @see com.viadeo.kasper.ddd.Entity
 */
public class NotSpecification<E extends Entity> extends AbstractSpecification<E> {

	private final EntitySpecification<E> spec;

	// ----------------------------------------------------------------------

	public NotSpecification(final EntitySpecification<E> spec) {
		super();
		
		this.spec = Preconditions.checkNotNull(spec);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.ddd.specification.impl.AbstractSpecification#isSatisfiedBy(com.viadeo.kasper.ddd.Entity)
	 */
	@Override
	public boolean isSatisfiedBy(final E entity) {
		return !this.spec.isSatisfiedBy(Preconditions.checkNotNull(entity));
	}
}
