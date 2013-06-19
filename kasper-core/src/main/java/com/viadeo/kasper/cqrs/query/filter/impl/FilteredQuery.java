// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IFilteredQuery;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.net.URL;

/**
 * Kasper filtered query base implementation
 * 
 * @param <DQO>
 */
public class FilteredQuery<DQO extends IQueryDQO<?>> implements IFilteredQuery<DQO> {

	private static final long serialVersionUID = 7761845519876972683L;
	private static final int PARAMETER_DQO_POSITION = 0;

	private IQueryFilter<DQO> filter;

	// ------------------------------------------------------------------------

	public FilteredQuery() {
	}

	public FilteredQuery(final IQueryFilter<DQO> filter) {
		this.filter = Preconditions.checkNotNull(filter);
	}

	public FilteredQuery(final URL url) {
		// TODO
		throw new UnsupportedOperationException();
	}

	// ------------------------------------------------------------------------

	@Override
	public Optional<IQueryFilter<DQO>> getFilter() {
		return Optional.fromNullable(this.filter);
	}

	@Override
	public void setFilter(final IQueryFilter<DQO> filter) {
		this.filter = Preconditions.checkNotNull(filter);
	}

	// ------------------------------------------------------------------------

	@Override
	public DQO dqo() {
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<DQO>> dqoClass = 
				(Optional<Class<DQO>>)
					ReflectionGenericsResolver.getParameterTypeFromClass(this.getClass(),
						FilteredQuery.class,	FilteredQuery.PARAMETER_DQO_POSITION);

		if (!dqoClass.isPresent()) {
			throw new KasperQueryException(
					"DQO type cannot by determined for "
							+ this.getClass().getName());
		}

		return QueryDQOFactory.get(dqoClass.get());
	}

}
