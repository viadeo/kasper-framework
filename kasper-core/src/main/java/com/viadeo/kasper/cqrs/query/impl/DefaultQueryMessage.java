// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.Query;

/**
 * The kasper query message base implementation
 *
 * @param <Q> the enclosed query type
 */
public class DefaultQueryMessage<Q extends Query> implements com.viadeo.kasper.cqrs.query.QueryMessage<Q> {

	private static final long serialVersionUID = 8648752933168387124L;
	private final Context context;
	private final Q query;

	// -----------------------------------------------------------------------

	public DefaultQueryMessage(final Context context, final Q query) {
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
	public Context getContext() {
		return this.context;
	}

}
