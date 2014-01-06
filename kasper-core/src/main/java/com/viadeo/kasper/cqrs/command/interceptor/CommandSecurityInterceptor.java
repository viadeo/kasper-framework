package com.viadeo.kasper.cqrs.command.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.BaseSecurityInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.security.SecurityConfiguration;

public class CommandSecurityInterceptor<C extends Command> extends BaseSecurityInterceptor
        implements Interceptor<C, C> {

    public CommandSecurityInterceptor(SecurityConfiguration securityConfiguration) {
        super(securityConfiguration);
    }

    @Override
    public C process(final C input, final Context context, final InterceptorChain<C, C> chain) throws Exception {
        addSecurityIdentity(context);
        return chain.next(input, context);
    }
}
