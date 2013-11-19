// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.Context;

/**
 * The kasper query message base implementation
 *
 * @param <Q> the enclosed query type
 */
public class QueryMessage<Q extends Query> {

	private static final long serialVersionUID = 8648752933168387124L;
	private final Context context;
	private final Q query;

	// -----------------------------------------------------------------------

	public QueryMessage(final Context context, final Q query) {
		this.context = Preconditions.checkNotNull(context);
		this.query = Preconditions.checkNotNull(query);
	}

	// -----------------------------------------------------------------------

	public Q getQuery() {
		return this.query;
	}

	// -----------------------------------------------------------------------

	public Context getContext() {
		return this.context;
	}

}
