// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

/**
 * A Kasper query filter
 *
 * @see Query
 */
public interface ResponseFilter<P extends QueryResult> extends QueryHandlerFilter {

    /**
     * Filter a Response after processing by its associated handler
     *
     * @param response the Response to be returned by the handler
     * @param context the context used to execute the handler
     *
     * @return the filtered response
     */
    <R extends QueryResponse<P>> R filter(final Context context, final R response);

}


