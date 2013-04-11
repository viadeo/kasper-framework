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
 * @see IQueryDQO
 * @see IQueryFilter
 */
public interface IQueryFilterNot<DQO extends IQueryDQO<?>> extends
IQueryFilter<DQO> {

	/**
	 * @return the enclosed filter to be inversed
	 */
	IQueryFilter<DQO> getFilter();

}
