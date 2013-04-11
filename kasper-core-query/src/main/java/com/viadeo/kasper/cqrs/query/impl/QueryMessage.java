// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryMessage;

/**
 * The kasper query message base implementation
 *
 * @param <Q> the enclosed query type
 */
public class QueryMessage<Q extends IQuery> implements IQueryMessage<Q> {

	private static final long serialVersionUID = 8648752933168387124L;
	private final IContext context;
	private final Q query;

	// -----------------------------------------------------------------------

	public QueryMessage(final IContext context, final Q query) {
		this.context = Preconditions.checkNotNull(context);
		this.query = Preconditions.checkNotNull(query);
	}

	// -----------------------------------------------------------------------

	@Override
	public Q getQuery() {
		return this.query;
	}

	// -----------------------------------------------------------------------

	@Override
	public IContext getContext() {
		return this.context;
	}

}
