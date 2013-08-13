// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.specification.ISpecification;

/**
 *
 * @param <T> The Object class
 * 
 * @see EntitySpecification
 * @see com.viadeo.kasper.ddd.Entity
 */
public class NotSpecification<T> extends Specification<T> {

	private final ISpecification<T> spec;

	// ----------------------------------------------------------------------

	public NotSpecification(final ISpecification<T> spec) {
		super();
		
		this.spec = Preconditions.checkNotNull(spec);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see Specification#isSatisfiedBy(Object)
	 */
	@Override
	public boolean isSatisfiedBy(final T entity) {
		return !this.spec.isSatisfiedBy(Preconditions.checkNotNull(entity));
	}
}
