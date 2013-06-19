// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;


/**
 * 
 *         A filter which inverse its enclosing filter
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see QueryDQO
 * @see QueryFilter
 */
public interface QueryFilterNot<DQO extends QueryDQO<?>> extends
        QueryFilter<DQO> {

	/**
	 * @return the enclosed filter to be inversed
	 */
	QueryFilter<DQO> getFilter();

}
