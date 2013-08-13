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

/**
 *
 * @param <T> Object class
 * 
 * @see EntitySpecification
 * @see com.viadeo.kasper.ddd.Entity
 */
public class AndSpecification<T> extends AbstractCompositeSpecification<T> {

	public AndSpecification(final ISpecification<T> spec1, final ISpecification<T> spec2) {
		super(spec1, spec2);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see Specification#isSatisfiedBy(Object)
	 */
	@Override
	public boolean isSatisfiedBy(final T object) {
		Preconditions.checkNotNull(object);
		return this.spec1.isSatisfiedBy(object) && this.spec2.isSatisfiedBy(object);
	}

	// ----------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.ddd.specification.impl.AbstractCompositeSpecification#isSatisfiedBy(Object, com.viadeo.kasper.ddd.specification.SpecificationErrorMessage)
	 */
	@Override
	public boolean isSatisfiedBy(final T object, final SpecificationErrorMessage errorMessage) {
		
		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage1 = new DefaultSpecificationErrorMessage();
		final boolean isSatisfied1 = this.spec1.isSatisfiedBy(object, errorMessage1);

		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage2 = new DefaultSpecificationErrorMessage();
		final boolean isSatisfied2 = this.spec2.isSatisfiedBy(object, errorMessage2);

		if (isSatisfied1 && isSatisfied2) {
			return true;
		}
		
		final StringBuffer sb = new StringBuffer();
		if (!isSatisfied1) {
			sb.append(errorMessage1.getMessage()).append("\n");
		}
		if (!isSatisfied2) {
			sb.append(errorMessage2.getMessage());
		}
		errorMessage.setMessage(sb.toString());
		
		return false;
	}

}
