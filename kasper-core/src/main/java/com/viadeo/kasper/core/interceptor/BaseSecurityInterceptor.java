package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import java.util.List;

public abstract class BaseSecurityInterceptor {
    private final List<IdentityElementContextProvider> identityElementContextProviders;

    public BaseSecurityInterceptor(SecurityConfiguration securityConfiguration) {
        this.identityElementContextProviders = securityConfiguration.getIdentityElementContextProviders();
    }

    protected void addSecurityIdentity(Context context) throws KasperSecurityException {
        for (IdentityElementContextProvider provider : identityElementContextProviders) {
            provider.provideIdentityElement(context);
        }
    }
}
