// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.interceptor.validation;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.cache.BaseValidationInterceptor;

import javax.validation.ValidatorFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryValidationInterceptor<Q extends Query, R extends QueryResult>
        extends BaseValidationInterceptor<Q>
        implements Interceptor<Q, QueryResponse<R>> {

    public QueryValidationInterceptor(final ValidatorFactory validatorFactory) {
        super(checkNotNull(validatorFactory));
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<R> process(final Q q,
                                    final Context context,
                                    final InterceptorChain<Q, QueryResponse<R>> chain) throws Exception {
        validate(q);
        return chain.next(q, context);
    }

}
