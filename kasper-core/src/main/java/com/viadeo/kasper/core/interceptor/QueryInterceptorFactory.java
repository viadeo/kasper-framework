// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

public abstract class QueryInterceptorFactory implements InterceptorFactory<Query, QueryResponse<QueryResult>> {

    protected abstract Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> doCreate(TypeToken<?> type);

    // ------------------------------------------------------------------------

    @Override
    public final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
        if ( ! accept(type)) {
            return Optional.absent();
        }
        return doCreate(type);
    }

    @Override
    public boolean accept(final TypeToken<?> type) {
        return QueryHandler.class.isAssignableFrom(type.getRawType());
    }

}
