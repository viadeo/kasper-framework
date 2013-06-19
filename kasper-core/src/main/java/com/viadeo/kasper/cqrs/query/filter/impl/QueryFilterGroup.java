// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @param <DQO> the associated Data Query Object
 * 
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup
 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter
 */
public class QueryFilterGroup<DQO extends QueryDQO<DQO>> extends
AbstractQueryFilter<DQO> implements com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup<DQO> {

	private static final long serialVersionUID = 7630593928554794374L;

	/**
	 * The associated filters list
	 */
	private List<QueryFilter<DQO>> filters;

	/**
	 * The operator to apply on filters group
	 */
	private Operator operator = Operator.AND;

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#isSatisfiedBy(com.viadeo.kasper.cqrs.query.QueryDTO)
	 */
	@Override
	public boolean isSatisfiedBy(final QueryDTO value) {
		Preconditions.checkNotNull(value);

		if (null != this.filters) {

			boolean satisfied = this.operator.equals(Operator.AND);

			for (final QueryFilter<DQO> filter : this.filters) {
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
			throw new KasperQueryException("No filter has been defined");
		}
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup#getFilters()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<QueryFilter<DQO>> getFilters() {
		if (null == this.filters) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(this.filters);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup#getOperator()
	 */
	@Override
	public Operator getOperator() {
		return this.operator;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup#reset()
	 */
	@Override
	public QueryFilterGroup reset() {
		this.filters = null;
		this.operator = Operator.AND;
		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup#filter(com.viadeo.kasper.cqrs.query.filter.QueryFilter)
	 */
	@Override
	public QueryFilterGroup filter(final QueryFilter<DQO> filter) {
		Preconditions.checkNotNull(filter);

		if (null == this.filters) {
			this.filters = new ArrayList<>();
		}

		this.filters.add(filter);

		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup#filter(com.viadeo.kasper.cqrs.query.filter.QueryFilter[])
	 */
	@Override
	public QueryFilterGroup filter(
			final QueryFilter<DQO>... filters) {
		for (final QueryFilter<DQO> filter : filters) {
			this.filter(filter);
		}
		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement#and(com.viadeo.kasper.cqrs.query.filter.QueryFilter)
	 */
	@Override
	public QueryFilterGroup and(final QueryFilter<DQO> filter) {
		this.filter(filter);
		this.and();

		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement#or(com.viadeo.kasper.cqrs.query.filter.QueryFilter)
	 */
	@Override
	public QueryFilterGroup or(final QueryFilter<DQO> filter) {
		this.filter(filter);
		this.or();

		return this;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement#and()
	 */
	@Override
	public QueryFilterGroup and() {
		this.operator = Operator.AND;
		return this;
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement#or()
	 */
	@Override
	public QueryFilterGroup or() {
		this.operator = Operator.OR;
		return this;
	}

}
