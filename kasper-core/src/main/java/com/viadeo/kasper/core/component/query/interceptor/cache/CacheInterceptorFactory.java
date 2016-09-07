// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor.cache;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
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
