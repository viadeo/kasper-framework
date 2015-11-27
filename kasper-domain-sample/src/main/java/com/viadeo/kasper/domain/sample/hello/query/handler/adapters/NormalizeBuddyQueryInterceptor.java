// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.query.handler.adapters;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.domain.sample.hello.api.query.GetAllHelloMessagesSentToBuddyQuery;

/**
 * This class allows to demonstrate the use of an interceptor by the GetAllHelloMessagesSentToBuddyQueryHandler handler.
 *
 * An interceptor can be applied explicitly for a given handler by using the XKasperQueryFilter annotation. Or it can be
 * registered on the platform builder in order to apply automatically to all handlers without explicit declaration.
 *
 */
public class NormalizeBuddyQueryInterceptor implements QueryInterceptor<Query, QueryResult> {

    @Override
    public QueryResponse<QueryResult> process(Query query, Context context, InterceptorChain<Query, QueryResponse<QueryResult>> chain) {
        Query newQuery = query;

        if (GetAllHelloMessagesSentToBuddyQuery.class.isAssignableFrom(query.getClass())) {
            final GetAllHelloMessagesSentToBuddyQuery castedQuery = (GetAllHelloMessagesSentToBuddyQuery) query;

            // Rewrite the query if needed
            if ( ! castedQuery.getForBuddy().toLowerCase().contentEquals(castedQuery.getForBuddy())) {
                newQuery = new GetAllHelloMessagesSentToBuddyQuery(
                        castedQuery.getForBuddy().toLowerCase()
                );
            }
        }

        return chain.next(newQuery, context);
    }
}
