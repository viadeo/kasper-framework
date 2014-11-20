// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.interceptor.cache.QueryCacheKeyGenerator;

import javax.cache.Cache;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class CacheInterceptor<Q extends Query, P extends QueryResult> implements Interceptor<Q, QueryResponse<P>> {

    private final Cache<Serializable, P> cache;
    private final XKasperQueryCache cacheAnnotation;
    private final QueryCacheKeyGenerator<Q> keyGenerator;

    // ------------------------------------------------------------------------

    public CacheInterceptor(final XKasperQueryCache cacheAnnotation,
                            final Cache<Serializable, P> cache,
                            final QueryCacheKeyGenerator<Q> keyGenerator) {
        this.cache = checkNotNull(cache);
        this.cacheAnnotation = checkNotNull(cacheAnnotation);
        this.keyGenerator = checkNotNull(keyGenerator);
    }

    // ------------------------------------------------------------------------


    @Override
    public QueryResponse<P> process(final Q q,
                                    final Context context,
                                    final InterceptorChain<Q, QueryResponse<P>> chain) throws Exception {

        final Serializable key = keyGenerator.computeKey(context.getUserID(), q, cacheAnnotation.keys());

        if (cache.containsKey(key) && (null != cache.get(key))) {
            return QueryResponse.of(cache.get(key));
        } else {
            final QueryResponse<P> response = chain.next(q, context);
            if (response.isOK()) {
                cache.put(key, response.getResult());
            }
            return response;
        }
    }

}
