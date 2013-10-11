// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.cache.impl;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.cache.QueryCacheKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnnotationQueryCacheActorFactory<QUERY extends Query, PAYLOAD extends QueryPayload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationQueryCacheActorFactory.class);

    private CacheManager cacheManager;

    // ------------------------------------------------------------------------

    public AnnotationQueryCacheActorFactory() {
        // uses the default configured cache manager
        try {
            this.cacheManager = Caching.getCacheManager();
        } catch (final IllegalStateException ise) {
            LOGGER.info("No cache manager available, if you want to enable cache support please provide an implementation of JCache - jsr 107.");
        }
    }

    public AnnotationQueryCacheActorFactory(final CacheManager cacheManager) {
        this.cacheManager = checkNotNull(cacheManager);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <QUERY extends Query, PAYLOAD extends QueryPayload> Optional<QueryCacheActor<QUERY, PAYLOAD>> make(
            final Class<QUERY> queryClass,
            final Class<? extends QueryService<QUERY, PAYLOAD>> queryServiceClass) {

        if (null != cacheManager) {
            final XKasperQueryService queryServiceAnnotation =
                    queryServiceClass.getAnnotation(XKasperQueryService.class);

            if (null != queryServiceAnnotation) {
                final XKasperQueryCache kasperQueryCache = queryServiceAnnotation.cache();

                if (kasperQueryCache.enabled()) {
                    final Cache<Serializable, QueryResult<PAYLOAD>> cache =
                            cacheManager.<Serializable, QueryResult<PAYLOAD>>
                             createCacheBuilder(queryClass.getName())
                            .setStoreByValue(false)
                            .setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
                                    new CacheConfiguration.Duration(
                                            TimeUnit.SECONDS,
                                            kasperQueryCache.ttl()
                                    )
                            )
                            .build();

                    return Optional.of(
                            new QueryCacheActor<>(
                                    kasperQueryCache,
                                    cache,
                                    createKeyGenerator(
                                            queryClass,
                                            (Class<? extends QueryCacheKeyGenerator<QUERY>>)
                                                    kasperQueryCache.keyGenerator()
                                    )
                            )
                    );
                }
            }
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    private <QUERY extends Query> QueryCacheKeyGenerator<QUERY> createKeyGenerator(
            final Class<QUERY> queryClass,
            final Class<? extends QueryCacheKeyGenerator<QUERY>> keyGenClass) {

        try {

            final TypeToken typeOfQuery = TypeToken
                    .of(keyGenClass)
                    .getSupertype(QueryCacheKeyGenerator.class)
                    .resolveType(
                            QueryCacheKeyGenerator.class.getTypeParameters()[0]
                    );

            if (!typeOfQuery.getRawType().isAssignableFrom(queryClass)) {
                throw new IllegalStateException(
                        String.format("Type %s in %s is not assignable from %s",
                            typeOfQuery.getRawType().getName(),
                            keyGenClass.getName(),
                            queryClass.getName()));
            }

            return keyGenClass.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
