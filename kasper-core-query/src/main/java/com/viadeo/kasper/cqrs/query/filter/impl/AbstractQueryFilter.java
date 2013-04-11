// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl;

import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;

/**
 * 
 *         This object can then be used in two manner : 1/ To validate against a
 *         specific DTO 2/ To generate a transformed form of the query for
 *         storage backends
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * @param <DTO>
 *            the associated Data Transfer Object
 * 
 * @see IQueryDQO
 * @see IQueryDTO
 * @see IQueryFilter
 */
public abstract class AbstractQueryFilter<DQO extends IQueryDQO<DQO>> implements IQueryFilter<DQO> {

	private static final long serialVersionUID = -6213663573013759149L;

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#and(com.viadeo.kasper.cqrs.query.filter.IQueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> and(
			final IQueryFilter<DQO> otherFilter) {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).and().filter(otherFilter);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#or(com.viadeo.kasper.cqrs.query.filter.IQueryFilter)
	 */
	@Override
	public QueryFilterGroup<DQO> or(
			final IQueryFilter<DQO> otherFilter) {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).or().filter(otherFilter);
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#and()
	 */
	@Override
	public QueryFilterGroup<DQO> and() {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).and();
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#or()
	 */
	@Override
	public QueryFilterGroup<DQO> or() {
		final QueryFilterGroup<DQO> group = new QueryFilterGroup<DQO>();
		return group.filter(this).or();
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#not()
	 */
	@Override
	public QueryFilterNot<DQO> not() {
		return new QueryFilterNot<DQO>(this);
	}

}
