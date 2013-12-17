// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.cqrs.Adapter;

/**
 * A Kasper query adapter
 *
 * @see Query
 */
public interface QueryAdapter<Q extends Query> extends QueryHandlerAdapter, Adapter<Q> { }


