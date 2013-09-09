// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.viadeo.kasper.cqrs.query.*;

/**
 *
 * This is a convenient class
 *
 * Extend it instead of implementing IQueryService will allow you to
 * override at your convenience retrieve(message) or simply retrieve(query)
 * if you are not interested by the message
 *
 * The query gateway is aware of this internal convenience and will deal with it
 *
 * @param <Q> the query
 * @param <PAYLOAD> the Result
 */
public abstract class AbstractQueryService<Q extends Query, PAYLOAD extends QueryPayload>
        implements QueryService<Q, PAYLOAD> {

    protected AbstractQueryService() { }

    @Override
    public QueryResult<PAYLOAD> retrieve(final QueryMessage<Q> message) throws Exception {
        return retrieve(message.getQuery());
    }

    public QueryResult<PAYLOAD> retrieve(final Q query) throws Exception {
        throw new UnsupportedOperationException();
    }

}

