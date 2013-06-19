// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;

import java.util.List;

/**
 * 
 *         A group filter, used to associate two other filters
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see QueryDQO
 * @see QueryFilter
 */
public interface QueryFilterGroup<DQO extends QueryDQO<?>> extends QueryFilter<DQO> {

	/**
	 * Supported group operators
	 */
	public static enum Operator {

		AND, OR

	}

	/**
	 * @return the filters grouped in this association
	 */
	List<QueryFilter<DQO>> getFilters();

	/**
	 * @return the operator used to associate all filters of this group
	 */
	Operator getOperator();

	/**
	 * @param filter
	 *            a filter to add into this filters association
	 * @return this instance
	 */
	QueryFilterGroup<DQO> filter(final QueryFilter<DQO> filter);

	/**
	 * @param filters
	 *            multiple filters to add into this association
	 * @return this instance
	 */
	QueryFilterGroup<DQO> filter(final QueryFilter<DQO>... filters);

	/**
	 * Reset associated filters list and operator
	 *
	 * @return this instance
	 */
	QueryFilterGroup<DQO> reset();

}
