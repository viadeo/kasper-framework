package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.IdentityContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

public abstract class BaseSecurityInterceptor {
    private final IdentityContextProvider identityContextProvider;

    public BaseSecurityInterceptor(final SecurityConfiguration securityConfiguration) {
        this.identityContextProvider = securityConfiguration.getIdentityContextProvider();
    }

    protected void addSecurityIdentity(final Context context) throws KasperSecurityException {
        identityContextProvider.provideIdentity(context);
    }
}
