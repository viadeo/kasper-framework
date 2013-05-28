// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;

import com.viadeo.kasper.cqrs.query.IQueryDTO;

import java.io.Serializable;

/**
 * 
 *         The base Query filter, used to parameterize query services
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see IQueryDQO
 */
public interface IQueryFilter<DQO extends IQueryDQO<?>> extends Serializable {

	/**
	 * Apply filter on a DTO instance and checks for conformance
	 * 
	 * @param dto
	 *            the DTO intance
	 * @return true if the DTO instance conforms to this filter
	 */
	boolean isSatisfiedBy(IQueryDTO dto);

	/**
	 * Compose a new group filter from this one and another one
	 * 
	 * @param otherFilter
	 *            a filter to combine with AND operator
	 * @return a new grouped filter
	 */
	IQueryFilterGroup<DQO> and(IQueryFilter<DQO> otherFilter);

	/**
	 * Compose a new group filter from this one and another one
	 * 
	 * @param otherFilter
	 *            a filter to combine with OR operator
	 * @return a new grouped filter
	 */
	IQueryFilterGroup<DQO> or(IQueryFilter<DQO> otherFilter);

	/**
	 * Compose a new group filter from this one
	 * 
	 * @return a new group filter with this filter set a a first component and
	 *         the operator set to AND
	 */
	IQueryFilterGroup<DQO> and();

	/**
	 * Compose a new group filter from this one
	 * 
	 * @return a new group filter with this filter set a a first component and
	 *         the operator set to OR
	 */
	IQueryFilterGroup<DQO> or();

	/**
	 * Inverse this filter
	 * 
	 * @return a new inversed filter
	 */
	IQueryFilterNot<DQO> not();

}
