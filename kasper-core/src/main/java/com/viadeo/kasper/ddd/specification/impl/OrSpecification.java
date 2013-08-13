// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.specification.SpecificationErrorMessage;
import com.viadeo.kasper.ddd.specification.ISpecification;

/**
 *
 * @param <T> The Object class
 * 
 * @see EntitySpecification
 * @see com.viadeo.kasper.ddd.Entity
 */
public class OrSpecification<T> extends AbstractCompositeSpecification<T> {

	public OrSpecification(final ISpecification<T> spec1, final ISpecification<T> spec2) {
		super(spec1, spec2);
	}

	// ----------------------------------------------------------------------

	@Override
	public boolean isSatisfiedBy(final T entity) {
		Preconditions.checkNotNull(entity);
		return this.spec1.isSatisfiedBy(entity) || this.spec2.isSatisfiedBy(entity);
	}

	// ----------------------------------------------------------------------
	
	@Override
	public boolean isSatisfiedBy(final T entity, final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage) {
		
		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage1 = new SpecificationErrorMessage();
		final boolean isSatisfied1 = this.spec1.isSatisfiedBy(entity, errorMessage1);

		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage2 = new SpecificationErrorMessage();
		final boolean isSatisfied2 = this.spec2.isSatisfiedBy(entity, errorMessage2);

		if (isSatisfied1 || isSatisfied2) {
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
