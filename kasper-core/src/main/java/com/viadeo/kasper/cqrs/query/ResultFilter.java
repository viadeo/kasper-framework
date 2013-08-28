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
public interface ResultFilter extends ServiceFilter {

    /**
     * Filter a Result after processing by its associated service
     *
     * @param result the Result to be returned by the service
     * @param context the context used to execute the service
     */
    <PS extends QueryPayload, PT extends PS> QueryResult<PT> filter(final Context context, final QueryResult<PS> result);

}


