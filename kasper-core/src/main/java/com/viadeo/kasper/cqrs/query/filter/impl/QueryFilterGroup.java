// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see IQueryDQO
 * @see IQueryFilterGroup
 * @see IQueryFilter
 */
public class QueryFilterGroup<DQO extends IQueryDQO<DQO>> extends
AbstractQueryFilter<DQO> implements IQueryFilterGroup<DQO> {

	private static final long serialVersionUID = 7630593928554794374L;

	/**
	 * The associated filters list
	 */
	private List<IQueryFilter<DQO>> filters;

	/**
	 * The operator to apply on filters group
	 */
	private Operator operator = Operator.AND;

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#isSatisfiedBy(com.viadeo.kasper.cqrs.query.IQueryDTO)
	 */
	@Override
	public boolean isSatisfiedBy(final IQueryDTO value) {
		Preconditions.checkNotNull(value);

		if (null != this.filters) {

			boolean satisfied = this.operator.equals(Operator.AND);

			for (final IQueryFilter<DQO> filter : this.filters) {
				final boolean localSatisfied = filter.isSatisfiedBy(value);
				if (this.operator.equals(Operator.AND) && !localSatisfied) {
					satisfied = false;
					break;
				} else if (this.operator.equals(Operator.OR) && localSatisfied) {
					satisfied = true;
					break;
				}
			}

			return satisfied;

		} else {
			throw new KasperQueryRuntimeException("No filter has been defined");
		}
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup#getFilters()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<IQueryFilter<DQO>> getFilters() {
		if (null == this.filters) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(this.filters);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup#getOperator()
	 */
	@Override
	public Operator getOperator() {
		return this.operator;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup#reset()
	 */
	@Override
	public QueryFilterGroup<DQO> reset() {
		this.filters = null;
		this.operator = Operator.AND;
		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup#filter(com.viadeo.kasper.cqrs.query.filter.IQueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> filter(final IQueryFilter<DQO> filter) {
		Preconditions.checkNotNull(filter);

		if (null == this.filters) {
			this.filters = new ArrayList<>();
		}

		this.filters.add(filter);

		return this;
	}

	/**
	 * @see
	 *      com.viadeo.kasper.cqrs.query.IKasperQueryFilterGroup#filter(com.viadeo
	 *      .kasper.cqrs.query.IKasperQueryFilter<DQO>...)
	 */
	@Override
	public QueryFilterGroup<DQO> filter(
			final IQueryFilter<DQO>... filters) {
		for (final IQueryFilter<DQO> filter : filters) {
			this.filter(filter);
		}
		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.impl.base.BaseQueryFilter#and(com.viadeo.kasper.cqrs.query.filter.IQueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> and(final IQueryFilter<DQO> filter) {
		this.filter(filter);
		this.and();

		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.impl.base.BaseQueryFilter#or(com.viadeo.kasper.cqrs.query.filter.IQueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> or(final IQueryFilter<DQO> filter) {
		this.filter(filter);
		this.or();

		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.impl.base.BaseQueryFilter#and()
	 */
	@Override
	public QueryFilterGroup<DQO> and() {
		this.operator = Operator.AND;
		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.impl.base.BaseQueryFilter#or()
	 */
	@Override
	public QueryFilterGroup<DQO> or() {
		this.operator = Operator.OR;
		return this;
	}

}
