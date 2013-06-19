// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;

import com.viadeo.kasper.cqrs.query.QueryDTO;

import java.io.Serializable;

/**
 * 
 *         The base Query filter, used to parameterize query services
 * 
 * @param <DQO> the associated Data Query Object
 * 
 * @see QueryDQO
 */
public interface QueryFilter<DQO extends QueryDQO<?>> extends Serializable {

	/**
	 * Apply filter on a DTO instance and checks for conformance
	 * 
	 * @param dto the DTO intance
	 * @return true if the DTO instance conforms to this filter
	 */
	boolean isSatisfiedBy(QueryDTO dto);

	/**
	 * Compose a new group filter from this one and another one
	 * 
	 * @param otherFilter a filter to combine with AND operator
	 * @return a new grouped filter
	 */
	QueryFilterGroup<DQO> and(QueryFilter<DQO> otherFilter);

	/**
	 * Compose a new group filter from this one and another one
	 * 
	 * @param otherFilter a filter to combine with OR operator
	 * @return a new grouped filter
	 */
	QueryFilterGroup<DQO> or(QueryFilter<DQO> otherFilter);

	/**
	 * Compose a new group filter from this one
	 * 
	 * @return a new group filter with this filter set a a first component and
	 *         the operator set to AND
	 */
	QueryFilterGroup<DQO> and();

	/**
	 * Compose a new group filter from this one
	 * 
	 * @return a new group filter with this filter set a a first component and
	 *         the operator set to OR
	 */
	QueryFilterGroup<DQO> or();

	/**
	 * Inverse this filter
	 * 
	 * @return a new inversed filter
	 */
	QueryFilterNot<DQO> not();

}
