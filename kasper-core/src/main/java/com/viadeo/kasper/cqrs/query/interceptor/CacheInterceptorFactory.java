// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.interceptor.cache.QueryCacheKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class CacheInterceptorFactory extends QueryInterceptorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheInterceptorFactory.class);

    private final CacheManager cacheManager;

    // ------------------------------------------------------------------------

    private static Optional<CacheManager> defaultCacheManager() {
        try {
            return Optional.of(Caching.getCacheManager());
        } catch (final IllegalStateException ise) {
            LOGGER.info("No cache manager available, if you want to enable cache support please provide an implementation of JCache - jsr 107.");
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public CacheInterceptorFactory() {
        this(defaultCacheManager());
    }

    private CacheInterceptorFactory(final Optional<CacheManager> optCacheManager) {
        if (checkNotNull(optCacheManager).isPresent()) {
            this.cacheManager = optCacheManager.get();
        } else {
            this.cacheManager = null;
        }
    }

    public CacheInterceptorFactory(final CacheManager cacheManager) {
        this.cacheManager = checkNotNull(cacheManager);
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    /**
     * Will not be called if cacheManager is null (accept() returns false)
     */
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {

        if (null == cacheManager) {
            return Optional.absent();
        }

        final Class<?> rawType = checkNotNull(type).getRawType();

        final XKasperQueryHandler queryHandlerAnnotation = rawType.getAnnotation(XKasperQueryHandler.class);
        final Class<? extends Query> queryClass =
                new QueryHandlerResolver().getQueryClass((Class<? extends QueryHandler>) rawType);

        final XKasperQueryCache annotation;
        if(null != queryHandlerAnnotation) {
            annotation = queryHandlerAnnotation.cache();
        } else {
            annotation = rawType.getAnnotation(XKasperQueryCache.class);
        }

        if (null != annotation) {
            if (annotation.enabled()) {

                final Cache<Serializable, QueryResult> cache =
                        cacheManager
                                .<Serializable, QueryResult> createCacheBuilder(queryClass.getName())
                                .setStoreByValue(false)
                                .setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
                                    new CacheConfiguration.Duration(
                                        TimeUnit.SECONDS,
                                        annotation.ttl()
                                    )
                                )
                                .build();

                final QueryCacheKeyGenerator<Query> keyGenerator = createKeyGenerator(
                    queryClass,
                    annotation.keyGenerator()
                );

                final Interceptor<Query, QueryResponse<QueryResult>> interceptor = new CacheInterceptor(
                    annotation,
                    cache,
                    keyGenerator
                );

                return Optional.of(InterceptorChain.makeChain(interceptor));
            }
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private QueryCacheKeyGenerator<Query> createKeyGenerator(
            final Class<? extends Query> queryClass,
            final Class<? extends QueryCacheKeyGenerator> keyGenClass) {

        try {

            final TypeToken typeOfQuery = TypeToken
                    .of(keyGenClass)
                    .getSupertype(QueryCacheKeyGenerator.class)
                    .resolveType(
                        QueryCacheKeyGenerator.class.getTypeParameters()[0]
                    );

            if ( ! typeOfQuery.getRawType().isAssignableFrom(queryClass)) {
                throw new IllegalStateException(
                    String.format("Type %s in %s is not assignable from %s",
                        typeOfQuery.getRawType().getName(),
                        keyGenClass.getName(),
                        queryClass.getName()
                    )
                );
            }

            return keyGenClass.newInstance();

        } catch (final InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
