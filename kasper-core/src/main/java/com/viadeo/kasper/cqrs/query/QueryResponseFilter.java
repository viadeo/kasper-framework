// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.cqrs.Adapter;

/**
 * A Kasper query response filter
 *
 * @see Query
 */
public interface QueryResponseFilter<P extends QueryResult> extends QueryHandlerFilter, Adapter<QueryResponse<P>> { }


