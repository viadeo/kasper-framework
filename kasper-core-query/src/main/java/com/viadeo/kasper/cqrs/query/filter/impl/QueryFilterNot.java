// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilterNot;

/**
 * 
 * @param <DQO>
 *            the associated Data Query Object
 * 
 * @see IQueryDQO
 * @see IQueryFilterNot
 * @see IQueryFilter
 */
public class QueryFilterNot<DQO extends IQueryDQO<DQO>> extends
AbstractQueryFilter<DQO> implements IQueryFilterNot<DQO> {

	private static final long serialVersionUID = 728076780149195258L;

	/**
	 * The enclosed inversed filter
	 */
	private final IQueryFilter<DQO> filter;

	// ------------------------------------------------------------------------

	public QueryFilterNot(final IQueryFilter<DQO> filter) {
		this.filter = Preconditions.checkNotNull(filter);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilter#isSatisfiedBy(com.viadeo.kasper.cqrs.query.IQueryDTO)
	 */
	@Override
	public boolean isSatisfiedBy(final IQueryDTO value) {
		return !this.filter.isSatisfiedBy(Preconditions.checkNotNull(value));
	}

	/**
	 * @see com.viadeo.kasper.cqrs.query.filter.IQueryFilterNot#getFilter()
	 */
	@Override
	public IQueryFilter<DQO> getFilter() {
		return this.filter;
	}

}
