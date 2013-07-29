// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.specification.EntitySpecification;
import com.viadeo.kasper.ddd.specification.annotation.XSpecification;

import java.util.Map;

/**
 *
 * @param <E> Entity
 */
public abstract class AbstractSpecification<E extends Entity> implements EntitySpecification<E> {

	/**
	 * Cache for all specifications annotation (if present)
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class<? extends AbstractSpecification>, XSpecification> ANNOTATIONS = Maps.newConcurrentMap();
	
	// ----------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.ddd.specification.EntitySpecification#isSatisfiedBy(com.viadeo.kasper.ddd.Entity)
	 */
	@Override
	public abstract boolean isSatisfiedBy(E entity);

	public boolean isSatisfiedBy(final E entity, final com.viadeo.kasper.ddd.specification.SpecificationErrorMessage errorMessage) {
		Preconditions.checkNotNull(errorMessage);
		final boolean isSatisfied = this.isSatisfiedBy(Preconditions.checkNotNull(entity));
		
		if (!isSatisfied) {
			final String message = getErrorMessage();
			errorMessage.setMessage(message);
		}
				
		return isSatisfied;
	}
	
	// ----------------------------------------------------------------------
	
	protected String getDefaultErrorMessage() {
		return this.getClass().getSimpleName() + " was not met";
	}
	
	protected String getErrorMessage() {
		final XSpecification annotation;
		final String errorMessage;
		
		if (!AbstractSpecification.ANNOTATIONS.containsKey(this.getClass())) {
			annotation = this.getClass().getAnnotation(XSpecification.class);
			AbstractSpecification.ANNOTATIONS.put(this.getClass(), annotation);
		} else {
			annotation = AbstractSpecification.ANNOTATIONS.get(this.getClass());
		}
		
		if (null != annotation) {			 
			if (!annotation.errorMessage().isEmpty()) {
				errorMessage = annotation.errorMessage();				
			} else {
				if (!annotation.description().isEmpty()) {
					errorMessage = "Specification not met : " + annotation.description();
				} else {
					errorMessage = getDefaultErrorMessage();
				}
			}
		} else {
			errorMessage = getDefaultErrorMessage();
		}
		
		return errorMessage;
	}
	
	// ----------------------------------------------------------------------

	@Override
	public EntitySpecification<E> and(final EntitySpecification<E> specification) {
		return new AndSpecification<>(this, Preconditions.checkNotNull(specification));
	}

	@Override
	public EntitySpecification<E> or(final EntitySpecification<E> specification) {
		return new OrSpecification<>(this, Preconditions.checkNotNull(specification));
	}

	@Override
	public EntitySpecification<E> not(final EntitySpecification<E> specification) {
		return new NotSpecification<>(Preconditions.checkNotNull(specification));
	}

}
