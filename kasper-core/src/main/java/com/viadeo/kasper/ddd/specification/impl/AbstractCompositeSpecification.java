// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.specification.ISpecification;
import com.viadeo.kasper.ddd.specification.SpecificationErrorMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @param <T> the object
 */
public abstract class AbstractCompositeSpecification<T> extends Specification<T> {

	protected final ISpecification<T> spec1;
	protected final ISpecification<T> spec2;

	// ----------------------------------------------------------------------

	public AbstractCompositeSpecification(final ISpecification<T> spec1, final ISpecification<T> spec2) {
		this.spec1 = checkNotNull(spec1);
		this.spec2 = checkNotNull(spec2);
	}
	
	// ----------------------------------------------------------------------

	/**
	 * Force reimplementation in child classes
	 */
	@Override
	public abstract boolean isSatisfiedBy(final T object, final SpecificationErrorMessage errorMessage);
	
}
