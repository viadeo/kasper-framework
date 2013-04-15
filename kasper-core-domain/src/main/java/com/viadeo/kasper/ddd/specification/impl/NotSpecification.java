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

/**
 *
 * @param <E> Entity
 * 
 * @see IEntitySpecification
 * @see IEntity
 */
public class NotSpecification<E extends IEntity> extends AbstractSpecification<E> {

	private final IEntitySpecification<E> spec;

	// ----------------------------------------------------------------------

	public NotSpecification(final IEntitySpecification<E> spec) {
		super();
		
		this.spec = Preconditions.checkNotNull(spec);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.ddd.specification.impl.AbstractSpecification#isSatisfiedBy(com.viadeo.kasper.ddd.IEntity)
	 */
	@Override
	public boolean isSatisfiedBy(final E entity) {
		return !this.spec.isSatisfiedBy(Preconditions.checkNotNull(entity));
	}
}
