// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.BaseValidationInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

import javax.validation.ValidatorFactory;

public class QueryValidationInterceptor<Q extends Query, R extends QueryResult>
        extends BaseValidationInterceptor<Q>
        implements Interceptor<Q, QueryResponse<R>> {

    // ------------------------------------------------------------------------

    public QueryValidationInterceptor(final ValidatorFactory validatorFactory) {
        super(validatorFactory);
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
