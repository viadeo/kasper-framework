// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryField;
import com.viadeo.kasper.cqrs.query.filter.QueryFilterElement;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.lang.reflect.Field;

/**
 * A base implementation for Kasper query field
 * 
 * @param <P> the field payload (Comparable)
 * 
 * @see com.viadeo.kasper.cqrs.query.filter.QueryField
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 */
public abstract class AbstractQueryField<P, DQO extends QueryDQO<DQO>, F extends QueryFilterElement<DQO, P>> implements QueryField<P, DQO, F> {

	/** The name of the field */
	private String name;
	private Class<F> filterClass;

	// ------------------------------------------------------------------------

	public AbstractQueryField() {

	}

	public AbstractQueryField(final String name) {
		this.name = Preconditions.checkNotNull(name);
	}

	private void initFilterClass() {
		if (null == this.filterClass) {
			
			@SuppressWarnings("unchecked") // Safe
			final Optional<Class<F>> optClass = 
				(Optional<Class<F>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(
							this.getClass(), QueryField.class, QueryField.PARAMETER_FILTER_POSITION);

			if (!optClass.isPresent()) {
				throw new KasperQueryException("Unable to find class for associated filter on " + this.getClass());
			}

			this.filterClass = optClass.get();
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Resolve DQO field from a specified DTO
	 * 
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryField#getFieldValue(com.viadeo.kasper.cqrs.query.QueryDTO)
	 */
	@Override
	public Optional<P> getFieldValue(final QueryDTO dto) {

		// Default behaviour, try to get DTO with field name ------------------
		// TODO: XPath implementation ?
		Field dtoField;
		try {

			dtoField = dto.getClass().getDeclaredField(this.name);
			dtoField.setAccessible(true);

			try {

				@SuppressWarnings("unchecked")
				final P value = (P) dtoField.get(dto);
				return Optional.fromNullable(value);

			} catch (final IllegalArgumentException | IllegalAccessException e) {
				throw new KasperQueryException(String.format(
						"Can't access field %s of DTO type %s",
						dtoField.getName(), dto.getClass().getName()), e);
			} catch (final ClassCastException e) {
				throw new KasperQueryException(String.format(
						"Bad comparison of field %s of DTO type %s",
						dtoField.getName(), dto.getClass().getName()), e);
			}

		} catch (final NoSuchFieldException e) {
			// Ignore, just return
		}

		return Optional.absent();
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryField#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryField#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryField#filter()
	 */
	@Override
	public F filter() {
		this.initFilterClass();
		try {
			final F filter = this.filterClass.newInstance();
			filter.field(this);
			return filter;
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new KasperQueryException("Unable to instantiate a new " + this.filterClass.getClass(), e);
		}
    }

	// ------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object otherField) {
		if (this == Preconditions.checkNotNull(otherField)) {
			return true;
		}
		if (this.getClass().isAssignableFrom(otherField.getClass())) {
			@SuppressWarnings("unchecked")
			// Safe
			final AbstractQueryField<P,DQO, F> other = (AbstractQueryField<P,DQO, F>) otherField;
			return Objects.equal(this.name, other.name);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (null != this.name) ? this.name.hashCode() : 0;
	}

}
