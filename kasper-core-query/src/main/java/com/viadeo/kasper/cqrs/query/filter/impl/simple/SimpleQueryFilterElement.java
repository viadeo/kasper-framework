// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl.simple;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryField;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryFilter;

/**
 *
 *         Simple affectation implementation for query filter element
 *
 * @param <DQO>
 *            the associated Data Query Object
 * @param <P>
 *            the managed payload type
 */
public class SimpleQueryFilterElement<DQO extends IQueryDQO<DQO>, P extends Comparable<P>>
		extends AbstractQueryFilter<DQO> implements IQueryFilterElement<DQO, P> {

	private static final long serialVersionUID = -818497044226174978L;

	/** the filter field for DTO resolution */
	private IQueryField<P, DQO, ? extends SimpleQueryFilterElement<DQO, P>> field;

	/** the operator to apply on */
	private SimpleFilterOperator operator;

	/** the value used in comparison with operator */
	private P value;

	// ------------------------------------------------------------------------

	public SimpleQueryFilterElement() {

	}

	public SimpleQueryFilterElement(final IQueryField<P, DQO, ? extends SimpleQueryFilterElement<DQO, P>> field) {
		this.field = Preconditions.checkNotNull(field);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#isSatisfiedBy(com.viadeo.kasper.cqrs.query.IQueryDTO)
	 */
	@Override
	public boolean isSatisfiedBy(final IQueryDTO dto) {
		Preconditions.checkNotNull(dto);

		if (null == this.field) {
			throw new KasperQueryRuntimeException(String.format("Field must be defined in %s", this.getClass().getName()));
		}

		final Optional<P> value = this.field.getFieldValue(dto);

		if (!value.isPresent()) {
			// Note: Perhaps we will authorize null values comparisons later in
			// some conditions
			throw new KasperQueryRuntimeException(
					"Trying to compare with null value from DTO");
		}

		return this.isSatisfiedBy(value.get());
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement#isSatisfiedBy(java.lang.Comparable)
	 */
	@Override
	public boolean isSatisfiedBy(final P value) {
		Preconditions.checkNotNull(value);

		if (null == this.operator) {
			throw new KasperQueryRuntimeException(
					"The operator has not been defined");
		}

		if (null == this.value) {
			throw new KasperQueryRuntimeException(
					"The comparison base value has not been defined");
		}

		// Normal Comparable operators ----------------------------------------

		if (this.operator.equals(SimpleFilterOperator.EQ)) {
			return (value.compareTo(this.value) == 0);
		}

		return false;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement#field(com.viadeo.kasper.cqrs.query.filter.IQueryField)
	 */
	@SuppressWarnings("unchecked") // Must be satisfied
	@Override
	public <FL extends IQueryFilterElement<DQO, P>> SimpleQueryFilterElement<DQO, P> field(final IQueryField<P, DQO, FL> field) {
		this.field = (IQueryField<P, DQO, ? extends SimpleQueryFilterElement<DQO, P>>) Preconditions.checkNotNull(field);
		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement#getField()
	 */
	@SuppressWarnings("unchecked") // Must be satisfied
	@Override
	public <F extends IQueryFilterElement<DQO, P>> Optional<IQueryField<P, DQO, F>> getField() {
		return Optional.fromNullable((IQueryField<P, DQO, F>) this.field);
	}

	// ------------------------------------------------------------------------

	/**
	 * Sets the operator
	 *
	 * @param operator
	 *            the operator to set
	 * @return this instance
	 */
	public SimpleQueryFilterElement<DQO, P> op(final SimpleFilterOperator operator) {
		this.operator = Preconditions.checkNotNull(operator);
		return this;
	}

	/**
	 * Sets the comparison value
	 *
	 * @param value
	 *            the comparison value
	 * @return this instance
	 */
	public SimpleQueryFilterElement<DQO, P> value(final P value) {
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	/**
	 * @return the operator
	 */
	public Optional<SimpleFilterOperator> getOperator() {
		return Optional.fromNullable(this.operator);
	}

	/**
	 * @return the comparison value
	 */
	public Optional<P> getValue() {
		return Optional.fromNullable(this.value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Parse an element filter definition and apply on this
	 *
	 * @param expr
	 *            the expression to parse
	 * @return this instance
	 */
	public SimpleQueryFilterElement<DQO, P> parse(final String expr) {
		Preconditions.checkNotNull(expr);
		// TODO
		return this;
	}

	// ------------------------------------------------------------------------

	public SimpleQueryFilterElement<DQO, P> equal(final P value) {
		this.operator = SimpleFilterOperator.EQ;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	// ------------------------------------------------------------------------

	public SimpleQueryFilterElement<DQO, P> equal() {
		this.operator = SimpleFilterOperator.EQ;
		return this;
	}

}
