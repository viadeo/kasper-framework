package com.viadeo.kasper.cqrs.query.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.*;

public class QueryCacheProcessor<Q extends Query, P extends QueryPayload> implements RequestProcessor<Q, QueryResult<P>> {

    @Override
    public QueryResult<P> process(Q q, Context context, RequestProcessorChain<Q, QueryResult<P>> chain) {
        return null;
    }
}
