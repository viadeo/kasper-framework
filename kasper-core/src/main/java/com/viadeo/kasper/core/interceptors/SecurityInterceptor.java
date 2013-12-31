package com.viadeo.kasper.core.interceptors;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryRequestActor;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.IdentityElementContextProvider;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.InterceptorChain;
import org.axonframework.domain.MetaData;
import org.axonframework.unitofwork.UnitOfWork;

import java.util.List;

public class SecurityInterceptor<Q extends Query, P extends QueryResult> implements QueryRequestActor<Q, P>, CommandDispatchInterceptor {

    private final List<IdentityElementContextProvider> identityElementContextProviders;

    public SecurityInterceptor(SecurityConfiguration securityConfiguration) {
        this.identityElementContextProviders = securityConfiguration.getIdentityElementContextProvider();
    }

    private void addSecurityIdentity(Context context) {
        for (IdentityElementContextProvider provider : identityElementContextProviders) {
            provider.provideIdentityElement(context);
        }
    }

    @Override
    public QueryResponse<P> process(Q q, Context context, RequestActorsChain<Q, QueryResponse<P>> chain) throws Exception {
        addSecurityIdentity(context);
        return chain.next(q, context);
    }

    @Override
    public CommandMessage<?> handle(CommandMessage<?> commandMessage) {
        MetaData metaData = commandMessage.getMetaData();
        Context context = (Context)metaData.get(Context.METANAME);
        addSecurityIdentity(context);
        return commandMessage;
    }
}
