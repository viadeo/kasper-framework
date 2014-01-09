// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

public class QueryHandlerInterceptorFactory extends QueryInterceptorFactory {

    private final QueryHandler<Query, QueryResult> queryHandler;

    public QueryHandlerInterceptorFactory(QueryHandler<Query, QueryResult> queryHandler) {
        this.queryHandler = queryHandler;
    }

    @Override
    public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(TypeToken<?> type) {
        return Optional.of(InterceptorChain.makeChain(new QueryHandlerInterceptor<>(queryHandler)));
    }
}
