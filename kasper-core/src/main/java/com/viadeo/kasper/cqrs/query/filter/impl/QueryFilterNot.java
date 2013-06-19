// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;

/**
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterNot
 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter
 */
public class QueryFilterNot<DQO extends QueryDQO<DQO>> extends
AbstractQueryFilter<DQO> implements com.viadeo.kasper.cqrs.query.filter.QueryFilterNot<DQO> {

	private static final long serialVersionUID = 728076780149195258L;

	/**
	 * The enclosed inversed filter
	 */
	private final QueryFilter<DQO> filter;

	// ------------------------------------------------------------------------

	public QueryFilterNot(final QueryFilter<DQO> filter) {
		this.filter = Preconditions.checkNotNull(filter);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilter#isSatisfiedBy(com.viadeo.kasper.cqrs.query.QueryDTO)
	 */
	@Override
	public boolean isSatisfiedBy(final QueryDTO value) {
		return !this.filter.isSatisfiedBy(Preconditions.checkNotNull(value));
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.QueryFilterNot#getFilter()
	 */
	@Override
	public QueryFilter<DQO> getFilter() {
		return this.filter;
	}

}
