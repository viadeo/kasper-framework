// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.IEntity;
import com.viadeo.kasper.ddd.specification.IEntitySpecification;
import com.viadeo.kasper.ddd.specification.ISpecificationErrorMessage;

/**
 *
 * @param <E> Entity
 * 
 * @see IEntitySpecification
 * @see IEntity
 */
public class AndSpecification<E extends IEntity> extends AbstractCompositeSpecification<E> {

	public AndSpecification(final IEntitySpecification<E> spec1, final IEntitySpecification<E> spec2) {
		super(spec1, spec2);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.ddd.specification.impl.AbstractSpecification#isSatisfiedBy(com.viadeo.kasper.ddd.IEntity)
	 */
	@Override
	public boolean isSatisfiedBy(final E entity) {
		Preconditions.checkNotNull(entity);
		return this.spec1.isSatisfiedBy(entity) && this.spec2.isSatisfiedBy(entity);
	}

	// ----------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.ddd.specification.impl.AbstractCompositeSpecification#isSatisfiedBy(com.viadeo.kasper.ddd.IEntity, com.viadeo.kasper.ddd.specification.ISpecificationErrorMessage)
	 */
	@Override
	public boolean isSatisfiedBy(final E entity, final ISpecificationErrorMessage errorMessage) {
		
		final ISpecificationErrorMessage errorMessage1 = new SpecificationErrorMessage();
		final boolean isSatisfied1 = this.spec1.isSatisfiedBy(entity, errorMessage1);

		final ISpecificationErrorMessage errorMessage2 = new SpecificationErrorMessage();
		final boolean isSatisfied2 = this.spec2.isSatisfiedBy(entity, errorMessage2);

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
