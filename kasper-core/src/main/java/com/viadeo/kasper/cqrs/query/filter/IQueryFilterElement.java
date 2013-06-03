// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;

import com.google.common.base.Optional;

/**
 * 
 *         A filter used to compare a specific DQO field to a value
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * @param <PAYLOAD>
 *            the payload of the filter
 * 
 * @see IQueryDQO
 * @see IQueryFilter
 */
public interface IQueryFilterElement<DQO extends IQueryDQO<?>, PAYLOAD> extends IQueryFilter<DQO> {

	/**
	 * Check if submitted value satisfies the filter
	 * 
	 * @param value
	 *            the value to filter
	 * @return true if the submitted value satisfies the filter
	 */
	boolean isSatisfiedBy(PAYLOAD value);

	/**
	 * Sets the filter DQO field to use for DTO value resolution Only necessary
	 * if isSatisfiedBy(DTO) is planned to be used
	 * 
	 * @param field
	 *            the DQO field to use
	 * @return this instance
	 */
	<FL extends IQueryFilterElement<DQO, PAYLOAD>> IQueryFilterElement<DQO, PAYLOAD> field(IQueryField<PAYLOAD, DQO, FL> field);

	/**
	 * @return the (optional) field used for DTO value resolution
	 */
	<F extends IQueryFilterElement<DQO, PAYLOAD>> Optional<IQueryField<PAYLOAD, DQO, F>> getField();

}
