// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.KasperSecurityException;
import com.viadeo.kasper.security.SecurityStrategy;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuerySecurityInterceptor<Q extends Query, R extends QueryResult>
        implements Interceptor<Q, QueryResponse<R>> {

    private SecurityStrategy securityStrategy;

    // ------------------------------------------------------------------------

    public QuerySecurityInterceptor(final SecurityStrategy securityStrategy) {
        this.securityStrategy = checkNotNull(securityStrategy);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResponse<R> process(final Q input,
                                    final Context context,
                                    final InterceptorChain<Q, QueryResponse<R>> chain)
            throws Exception {

        try {
            securityStrategy.beforeRequest(context);
        } catch (KasperSecurityException e) {
            //temporary commented the return error before real interceptor activation
            LOGGER.warn(String.format("%s generated error : %s", input.toString(), e.getKasperReason()));
            //return QueryResponse.error(e.getKasperReason());
        }

        final QueryResponse<R> queryResponse = chain.next(input, context);
        securityStrategy.afterRequest();
        return queryResponse;
    }

}
