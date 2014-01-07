package com.viadeo.kasper.cqrs.query.interceptor;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.BaseSecurityInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.SecurityConfiguration;

public class QuerySecurityInterceptor<Q extends Query, R extends QueryResult> extends BaseSecurityInterceptor
        implements Interceptor<Q, QueryResponse<R>> {

    public QuerySecurityInterceptor(SecurityConfiguration securityConfiguration) {
        super(securityConfiguration);
    }

    @Override
    public QueryResponse<R> process(final Q input,
                                    final Context context,
                                    final InterceptorChain<Q, QueryResponse<R>> chain) throws Exception {
        try {
            addSecurityIdentity(context);
        } catch (KasperSecurityException e) {
            return QueryResponse.error(
                    new KasperReason(
                            CoreReasonCode.INVALID_INPUT.name(),
                            e.getMessage()
                    ));
        }
        return chain.next(input, context);
    }
}
