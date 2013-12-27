package com.viadeo.kasper.core.interceptors;

import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.IdentityElementContextProvider;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.InterceptorChain;
import org.axonframework.unitofwork.UnitOfWork;

import java.util.List;

public class SecurityInterceptor<Q extends Query, P extends QueryResult> implements QueryRequestActor<Q, P>, CommandHandlerInterceptor {

    private final List<IdentityElementContextProvider> identityElementContextProviders;

    public SecurityInterceptor(com.viadeo.kasper.security.SecurityConfiguration securityConfiguration) {
        this.identityElementContextProviders = securityConfiguration.getIdentityElementContextProvider();
    }



    private void addSecurityIdentity(Context context) {
        for (IdentityElementContextProvider provider : identityElementContextProviders) {
            provider.provideIdentityElement(context);
        }
    }

    @Override
    //TODO: Needs access to Context
    public Object handle(CommandMessage<?> commandMessage, UnitOfWork unitOfWork, InterceptorChain interceptorChain) throws Throwable {
        return interceptorChain.proceed();
    }

    @Override
    public QueryResponse<P> process(Q q, Context context, RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        addSecurityIdentity(context);
        return chain.next(q, context);
    }
}
