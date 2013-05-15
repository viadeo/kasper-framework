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
 * @param <E> the entity
 */
public abstract class AbstractCompositeSpecification<E extends IEntity> 
		extends AbstractSpecification<E> {

	protected final IEntitySpecification<E> spec1;
	protected final IEntitySpecification<E> spec2;

	// ----------------------------------------------------------------------

	public AbstractCompositeSpecification(final IEntitySpecification<E> spec1, final IEntitySpecification<E> spec2) {
		this.spec1 = Preconditions.checkNotNull(spec1);
		this.spec2 = Preconditions.checkNotNull(spec2);
	}
	
	// ----------------------------------------------------------------------

	/**
	 * Force reimplementation in child classes
	 */
	@Override
	public abstract boolean isSatisfiedBy(final E entity, final ISpecificationErrorMessage errorMessage);
	
}
