// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;

/**
 * A filtered Kasper query
 *
 * @param <DQO> the Data Query Object class this filter will apply on
 * 
 */
public interface IFilteredQuery<DQO extends IQueryDQO<?>> extends IQuery {

	/**
	 * @return the (optional) filter used by this query
	 */
	Optional<IQueryFilter<DQO>> getFilter();

	/**
	 * @param filter the filter to be used by this query
	 */
	void setFilter(IQueryFilter<DQO> filter);

	/**
	 * @return a new DQO instance to be used for building a filter for this query
	 */
	DQO dqo();

}
