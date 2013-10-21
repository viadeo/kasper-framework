// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.cache.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.cache.QueryCacheKeyGenerator;

import javax.cache.Cache;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryCacheActor<Q extends Query, P extends QueryResult>
        implements QueryRequestActor<Q, P> {

    private final Cache<Serializable, QueryResponse<P>> cache;
    private final XKasperQueryCache cacheAnnotation;
    private final QueryCacheKeyGenerator<Q> keyGenerator;

    // ------------------------------------------------------------------------

    public QueryCacheActor(final XKasperQueryCache cacheAnnotation,
                           final Cache<Serializable, QueryResponse<P>> cache,
                           final QueryCacheKeyGenerator<Q> keyGenerator) {
        this.cache = checkNotNull(cache);
        this.cacheAnnotation = checkNotNull(cacheAnnotation);
        this.keyGenerator = checkNotNull(keyGenerator);
    }

    // ------------------------------------------------------------------------


    @Override
    public QueryResponse<P> process(Q q, Context context, RequestActorsChain<Q, QueryResponse<P>> chain) throws
            Exception {
        final Serializable key = keyGenerator.computeKey(cacheAnnotation, q);

        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            final QueryResponse<P> response = chain.next(q, context);
            cache.put(key, response);
            return response;
        }
    }

}
