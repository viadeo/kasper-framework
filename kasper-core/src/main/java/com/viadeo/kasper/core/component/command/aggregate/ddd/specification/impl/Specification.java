// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification.impl;

import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.ISpecification;
import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.SpecificationErrorMessage;
import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.annotation.XKasperSpecification;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @param <T> The object class
 */
public abstract class Specification<T> implements ISpecification<T> {

	/**
	 * Cache for all specifications annotation (if present)
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class<? extends Specification>, XKasperSpecification> ANNOTATIONS =
            Maps.newConcurrentMap();
	
	// ----------------------------------------------------------------------
	
	/**
	 * @see EntitySpecification#isSatisfiedBy(Object)
	 */
	@Override
	public abstract boolean isSatisfiedBy(final T entity);

	public boolean isSatisfiedBy(final T entity, final SpecificationErrorMessage errorMessage) {
		checkNotNull(errorMessage);
		final boolean isSatisfied = this.isSatisfiedBy(checkNotNull(entity));
		
		if ( ! isSatisfied) {
			final String message = getErrorMessage(entity);
			errorMessage.setMessage(message);
		}
				
		return isSatisfied;
	}
	
	// ----------------------------------------------------------------------
	
	protected String getDefaultErrorMessage(final T entity) {
		return this.getClass().getSimpleName() + " specification was not met";
	}
	
	protected String getErrorMessage(final T entity) {
		final XKasperSpecification annotation;
		String errorMessage;
		
		if ( ! Specification.ANNOTATIONS.containsKey(this.getClass())) {
			annotation = this.getClass().getAnnotation(XKasperSpecification.class);
            if (null != annotation) {
			    Specification.ANNOTATIONS.put(this.getClass(), annotation);
            }
		} else {
			annotation = Specification.ANNOTATIONS.get(this.getClass());
		}
		
		if (null != annotation) {			 
			if ( ! annotation.errorMessage().isEmpty()) {
				errorMessage = annotation.errorMessage();				
			} else {
				if ( ! annotation.description().isEmpty()) {
					errorMessage = "Specification not met : " + annotation.description();
				} else {
					errorMessage = getDefaultErrorMessage(entity);
				}
			}
		} else {
			errorMessage = getDefaultErrorMessage(entity);
		}

        if (String.class.equals(entity.getClass())) {
            errorMessage += " for value " + entity.toString();
        }
		
		return errorMessage;
	}
	
	// ----------------------------------------------------------------------

	@Override
	public ISpecification<T> and(final ISpecification<T> specification) {
		return new AndSpecification<>(this, checkNotNull(specification));
	}

	@Override
	public ISpecification<T> or(final ISpecification<T> specification) {
		return new OrSpecification<>(this, checkNotNull(specification));
	}

	@Override
	public ISpecification<T> not() {
		return new NotSpecification<>(this);
	}

}
