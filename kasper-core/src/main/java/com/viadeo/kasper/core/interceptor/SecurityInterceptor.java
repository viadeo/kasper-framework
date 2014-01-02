package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import java.util.List;

public class SecurityInterceptor<INPUT, OUTPUT> implements  Interceptor<INPUT, OUTPUT>  {

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
    public OUTPUT process(INPUT input, Context context, InterceptorChain<INPUT, OUTPUT> chain) throws Exception {
        addSecurityIdentity(context);
        return chain.next(input, context);
    }
}
