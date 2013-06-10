// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl.base;

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
 *         Base implementation for query filter element
 *
 * @param <DQO>
 *            the associated Data Query Object
 * @param <P>
 *            the managed payload type
 */
public class BaseQueryFilterElement<DQO extends IQueryDQO<DQO>, P extends Comparable<P>>
		extends AbstractQueryFilter<DQO> implements IQueryFilterElement<DQO, P> {

	private static final long serialVersionUID = -818497044226142978L;

	/** the filter field for DTO resolution */
	private IQueryField<P, DQO, ? extends BaseQueryFilterElement<DQO, P>> field;

	/** the operator to apply on */
	private BaseFilterOperator operator;

	/** the value used in comparison with operator */
	private P value;

	// ------------------------------------------------------------------------

	public BaseQueryFilterElement() {

	}

	public BaseQueryFilterElement(final IQueryField<P, DQO, ? extends BaseQueryFilterElement<DQO, P>> field) {
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

		final Optional<P> valueOpt = this.field.getFieldValue(dto);

		if (!valueOpt.isPresent()) {
			// Note: Perhaps we will authorize null values comparisons later in
			// some conditions
			throw new KasperQueryRuntimeException(
					"Trying to compare with null value from DTO");
		}

		return this.isSatisfiedBy(valueOpt.get());
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement#isSatisfiedBy(Object)
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

		if (this.operator.equals(BaseFilterOperator.EQ)) {
			return (value.compareTo(this.value) == 0);
		}

		if (this.operator.equals(BaseFilterOperator.NE)) {
			return (value.compareTo(this.value) != 0);
		}

		if (this.operator.equals(BaseFilterOperator.LT)) {
			return (value.compareTo(this.value) < 0);
		}

		if (this.operator.equals(BaseFilterOperator.GT)) {
			return (value.compareTo(this.value) > 0);
		}

		if (this.operator.equals(BaseFilterOperator.LE)) {
			return (value.compareTo(this.value) <= 0);
		}

		if (this.operator.equals(BaseFilterOperator.GE)) {
			return (value.compareTo(this.value) >= 0);
		}

		// String specific operators ------------------------------------------

		if (String.class.isInstance(value)) {
			final String strThisVal = String.class.cast(this.value);
			final String strVal = String.class.cast(value);

			if (this.operator.equals(BaseFilterOperator.CONTAINS)) {
				return strVal.contains(strThisVal);
			}

			if (this.operator.equals(BaseFilterOperator.STARTSWITH)) {
				return strVal.startsWith(strThisVal);
			}

			if (this.operator.equals(BaseFilterOperator.ENDSWITH)) {
				return strVal.endsWith(strThisVal);
			}

		} else {
			throw new KasperQueryRuntimeException(String.format(
					"Operator %s cannot be applied to %s", this.operator,
					this.value.getClass().getSimpleName()));
		}

		return false;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterElement#field(com.viadeo.kasper.cqrs.query.filter.IQueryField)
	 */
	@SuppressWarnings("unchecked") // Must be satisfied
	@Override
	public <FL extends IQueryFilterElement<DQO, P>> BaseQueryFilterElement<DQO, P> field(final IQueryField<P, DQO, FL> field) {
		this.field = (IQueryField<P, DQO, ? extends BaseQueryFilterElement<DQO, P>>) Preconditions.checkNotNull(field);
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
	public BaseQueryFilterElement<DQO, P> op(final BaseFilterOperator operator) {
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
	public BaseQueryFilterElement<DQO, P> value(final P value) {
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	/**
	 * @return the operator
	 */
	public Optional<BaseFilterOperator> getOperator() {
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
	public BaseQueryFilterElement<DQO, P> parse(final String expr) {
		Preconditions.checkNotNull(expr);
		// TODO
		return this;
	}

	// ------------------------------------------------------------------------

	public BaseQueryFilterElement<DQO, P> equal(final P value) {
		this.operator = BaseFilterOperator.EQ;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> notEqual(final P value) {
		this.operator = BaseFilterOperator.NE;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> lessThan(final P value) {
		this.operator = BaseFilterOperator.LT;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> greaterThan(final P value) {
		this.operator = BaseFilterOperator.GT;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> ltOrEqual(final P value) {
		this.operator = BaseFilterOperator.LE;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> gtOrEqual(final P value) {
		this.operator = BaseFilterOperator.GE;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> contains(final P value) {
		this.operator = BaseFilterOperator.CONTAINS;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> startsWith(final P value) {
		this.operator = BaseFilterOperator.STARTSWITH;
		this.value = Preconditions.checkNotNull(value);
		return this;
	}

	public BaseQueryFilterElement<DQO, P> endsWith(final P value) {
		this.operator = BaseFilterOperator.ENDSWITH;
		this.value = Preconditions.checkNotNull(value);

		return this;
	}

	// ------------------------------------------------------------------------

	public BaseQueryFilterElement<DQO, P> equal() {
		this.operator = BaseFilterOperator.EQ;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> notEqual() {
		this.operator = BaseFilterOperator.NE;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> lessThan() {
		this.operator = BaseFilterOperator.LT;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> greaterThan() {
		this.operator = BaseFilterOperator.GT;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> ltOrEqual() {
		this.operator = BaseFilterOperator.LE;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> gtOrEqual() {
		this.operator = BaseFilterOperator.GE;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> contains() {
		this.operator = BaseFilterOperator.CONTAINS;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> startsWith() {
		this.operator = BaseFilterOperator.STARTSWITH;
		return this;
	}

	public BaseQueryFilterElement<DQO, P> endsWith() {
		this.operator = BaseFilterOperator.ENDSWITH;
		return this;
	}

}
