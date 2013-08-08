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
 * @param <E> the entity
 */
public abstract class AbstractCompositeSpecification<E extends Entity>
		extends AbstractSpecification<E> {

	protected final EntitySpecification<E> spec1;
	protected final EntitySpecification<E> spec2;

	// ----------------------------------------------------------------------

	public AbstractCompositeSpecification(final EntitySpecification<E> spec1, final EntitySpecification<E> spec2) {
		this.spec1 = Preconditions.checkNotNull(spec1);
		this.spec2 = Preconditions.checkNotNull(spec2);
	}
	
	// ----------------------------------------------------------------------

	/**
	 * Force reimplementation in child classes
	 */
	@Override
	public abstract boolean isSatisfiedBy(final E entity, final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage);
	
}
