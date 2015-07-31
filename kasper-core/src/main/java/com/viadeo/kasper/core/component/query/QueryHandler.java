// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.Handler;

/**
 * A <code>QueryHandler</code> is invoked to process a <code>Query</code> request in order to provide a <code>Result</code>.
 *
 * @param <QUERY> the query class handled by this <code>QueryHandler</code>.
 * @param <RESULT> the result returned by this <code>QueryHandler</code>
 *
 * @see Query
 * @see QueryResult
 * @see QueryResponse
 * @see Context
 */
public interface QueryHandler<QUERY extends Query, RESULT extends QueryResult>
        extends Handler<QueryResponse<RESULT>, QUERY>
{

    /**
     * Generic parameter position for Data Query Object
     */
    public static final int PARAMETER_QUERY_POSITION = 0;

    /**
     * Generic parameter position for Data Transfer Object
     */
    public static final int PARAMETER_RESULT_POSITION = 1;

    /**
     * Handle the <code>Query</code> with his <code>Context</code>.
     *
     * @param context the context related to the request
     * @param query the query requested
     * @return a response
     */
    @Override
    QueryResponse<RESULT> handle(Context context, QUERY query);

    /**
     * @return the result class returned by this <code>QueryHandler</code>.
     */
    Class<RESULT> getResultClass();

    @Override
    Class<? extends QueryHandler> getHandlerClass();
}
