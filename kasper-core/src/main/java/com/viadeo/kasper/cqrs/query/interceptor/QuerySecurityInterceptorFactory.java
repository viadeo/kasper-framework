package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.interceptor.CommandSecurityInterceptor;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuerySecurityInterceptorFactory extends QueryInterceptorFactory {
    private SecurityConfiguration securityConfiguration;

    public QuerySecurityInterceptorFactory(SecurityConfiguration securityConfiguration) {
        checkNotNull(securityConfiguration);
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    protected Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> doCreate(final TypeToken<?> type) {
        final Interceptor<Query, QueryResponse<QueryResult>> interceptor;

        interceptor = new QuerySecurityInterceptor(securityConfiguration);
        return Optional.of(InterceptorChain.makeChain(interceptor));
    }
}
