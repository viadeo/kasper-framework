package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;

import javax.cache.*;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class QueryCacheProcessor<Q extends Query, P extends QueryPayload> implements RequestProcessor<Q, QueryResult<P>> {
    private final Cache<Serializable, QueryResult<P>> cache;
    private final XKasperQueryCache cacheAnnotation;
    private final QueryCacheKeyGenerator<Q> keyGenerator;

    public QueryCacheProcessor(XKasperQueryCache cacheAnnotation, Cache<Serializable, QueryResult<P>> cache, QueryCacheKeyGenerator<Q> keyGenerator) {
        this.cache = cache;
        this.cacheAnnotation = cacheAnnotation;
        this.keyGenerator = keyGenerator;
    }


    public static class AnnotationQueryCacheProcessorFactory {
        private final CacheManager cacheManager;

        public AnnotationQueryCacheProcessorFactory(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        public <QUERY extends Query, PAYLOAD extends QueryPayload> RequestProcessor<QUERY, QueryResult<PAYLOAD>> make(Class<QUERY> queryClass, Class<? extends QueryService<QUERY, PAYLOAD>> queryServiceClass) {
            XKasperQueryService queryServiceAnnotation = queryServiceClass.getAnnotation(XKasperQueryService.class);
            XKasperQueryCache kasperQueryCache = queryServiceAnnotation.cache();

            if (kasperQueryCache.enabled()) {
                Cache<Serializable, QueryResult<PAYLOAD>> cache = cacheManager.<Serializable, QueryResult<PAYLOAD>>createCacheBuilder(queryClass.getName())
                        .setStoreByValue(false)
                        .setExpiry(CacheConfiguration.ExpiryType.MODIFIED, new CacheConfiguration.Duration(TimeUnit.SECONDS, kasperQueryCache.ttl()))
                        .build();
                return new QueryCacheProcessor<QUERY, PAYLOAD>(kasperQueryCache, cache, createKeyGenerator(queryClass, kasperQueryCache.keyGenerator()));
            }

            return new DelegatingRequestProcessor<>();
        }

        private <QUERY extends Query> QueryCacheKeyGenerator<QUERY> createKeyGenerator(Class<QUERY> queryClass, Class<? extends QueryCacheKeyGenerator> keyGenClass) {
            try {
                final TypeToken<?> typeOfQuery = TypeToken.of(keyGenClass).getSupertype(QueryCacheKeyGenerator.class).resolveType(QueryCacheKeyGenerator.class.getTypeParameters()[0]);

                if (!typeOfQuery.getRawType().isAssignableFrom(queryClass)) {
                    throw new IllegalStateException("Type " + typeOfQuery.getRawType().getName() + " in " + keyGenClass.getName() + " is not assignable from " + queryClass.getName());
                }

                return keyGenClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public QueryResult<P> process(Q q, Context context, RequestProcessorChain<Q, QueryResult<P>> chain) throws
            Exception {
        final Serializable key = keyGenerator.computeKey(cacheAnnotation, q);

        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            QueryResult<P> result = chain.next(q, context);
            cache.put(key, result);
            return result;
        }
    }
}
