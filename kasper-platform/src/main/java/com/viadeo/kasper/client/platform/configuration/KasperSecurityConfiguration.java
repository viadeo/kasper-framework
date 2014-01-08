package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.security.IdentityContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration implements SecurityConfiguration {

    private final IdentityContextProvider identityContextProviders;

    public KasperSecurityConfiguration(IdentityContextProvider identityContextProviders) {
        checkNotNull(identityContextProviders);
        this.identityContextProviders = identityContextProviders;
    }

    @Override
    public IdentityContextProvider getIdentityContextProvider() {
        return identityContextProviders;
    }
}
