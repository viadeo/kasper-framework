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
public class OrSpecification<E extends Entity> extends AbstractCompositeSpecification<E> {

	public OrSpecification(final EntitySpecification<E> spec1, final EntitySpecification<E> spec2) {
		super(spec1, spec2);
	}

	// ----------------------------------------------------------------------

	@Override
	public boolean isSatisfiedBy(final E entity) {
		Preconditions.checkNotNull(entity);
		return this.spec1.isSatisfiedBy(entity) || this.spec2.isSatisfiedBy(entity);
	}

	// ----------------------------------------------------------------------
	
	@Override
	public boolean isSatisfiedBy(final E entity, final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage) {
		
		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage1 = new DefaultSpecificationErrorMessage();
		final boolean isSatisfied1 = this.spec1.isSatisfiedBy(entity, errorMessage1);

		final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage2 = new DefaultSpecificationErrorMessage();
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
