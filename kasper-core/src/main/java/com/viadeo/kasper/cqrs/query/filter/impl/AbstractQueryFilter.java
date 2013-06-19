// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl;

import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;

/**
 * 
 * This object can then be used in two manner : 1/ To validate against a
 * specific DTO 2/ To generate a transformed form of the query for
 * storage backends
 * 
 * @param <DQO> the associated Data Query Object
 *
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 * @see com.viadeo.kasper.cqrs.query.QueryDTO
 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter
 */
public abstract class AbstractQueryFilter<DQO extends QueryDQO<DQO>> implements QueryFilter<DQO> {

	private static final long serialVersionUID = -6213663573013759149L;

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#and(com.viadeo.kasper.cqrs.query.filter.QueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> and(
			final QueryFilter<DQO> otherFilter) {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).and().filter(otherFilter);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#or(com.viadeo.kasper.cqrs.query.filter.QueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> or(
			final QueryFilter<DQO> otherFilter) {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).or().filter(otherFilter);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#and()
	 */
	@Override
	public QueryFilterGroup<DQO> and() {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).and();
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#or()
	 */
	@Override
	public QueryFilterGroup<DQO> or() {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).or();
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#not()
	 */
	@Override
	public QueryFilterNot<DQO> not() {
		return new QueryFilterNot<DQO>(this);
	}

}
