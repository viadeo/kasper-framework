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
public interface QueryFilter extends ServiceFilter {

    /**
     * Filter a query before processing by its associated service
     *
     * @param query the query to be processed
     * @param context the context used to execute the service
     */
	<QS extends Query, QT extends QS> QT filter(Context context, QS query);

}


