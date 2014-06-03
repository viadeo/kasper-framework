// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import com.viadeo.kasper.security.strategy.impl.DefaultPublicSecurityStrategy;
import com.viadeo.kasper.security.strategy.impl.DefaultSecurityStrategy;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuerySecurityInterceptorFactory extends QueryInterceptorFactory {

    private final SecurityConfiguration securityConfiguration;
    private final Map<Class, SecurityStrategy> strategies = Maps.newHashMap();

    // ------------------------------------------------------------------------

    public QuerySecurityInterceptorFactory(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        final Class<? extends Query> queryClass = extractQueryClassFromTypeToken(type);
        final SecurityStrategy securityStrategy;

        if (strategies.containsKey(queryClass)) {
            securityStrategy = strategies.get(queryClass);
        } else {
            if (queryClass.isAnnotationPresent(XKasperPublic.class)) {
                securityStrategy = new DefaultPublicSecurityStrategy(securityConfiguration, queryClass);
            } else {
                securityStrategy = new DefaultSecurityStrategy(securityConfiguration, queryClass);
            }
            strategies.put(queryClass, securityStrategy);
        }

        final Interceptor<Query, QueryResponse<QueryResult>> interceptor =
                new QuerySecurityInterceptor<>(securityStrategy);
        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Query> extractQueryClassFromTypeToken(final TypeToken<?> type) {
        final Class<?> rawType = checkNotNull(type).getRawType();
        return new QueryHandlerResolver().getQueryClass(
                (Class<? extends QueryHandler>) rawType
        );
    }

}
