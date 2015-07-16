// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

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
		this.context = checkNotNull(context);
		this.query = checkNotNull(query);
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
