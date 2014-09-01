// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.QueryHandlerResolver;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.cqrs.ApplicationIdInterceptor;
import com.viadeo.kasper.security.strategy.SecurityStrategy;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuerySecurityInterceptorFactory extends QueryInterceptorFactory {

    private SecurityStrategy securityStrategy;

    // ------------------------------------------------------------------------

    public QuerySecurityInterceptorFactory() {
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        final Class<? extends Query> queryClass = extractQueryClassFromTypeToken(type);

        if (null == this.securityStrategy) {
            this.securityStrategy = new SecurityStrategy(queryClass);
        }

        ApplicationIdInterceptor applicationIdInterceptor = new ApplicationIdInterceptor();
        //applicationIdInterceptor.process(queryClass, );

        final Interceptor<Query, QueryResponse<QueryResult>> interceptor =
                new QuerySecurityInterceptor<>(this.securityStrategy);
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
