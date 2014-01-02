package com.viadeo.kasper.client.platform.configuration;

import com.google.common.collect.Lists;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration implements SecurityConfiguration {

    private final List<IdentityElementContextProvider> identityElementContextProviders = Lists.newArrayList();

    @Override
    public void addIdentityElementContextProvider(IdentityElementContextProvider provider) {
        checkNotNull(provider);
        identityElementContextProviders.add(provider);
    }

    @Override
    public List<IdentityElementContextProvider> getIdentityElementContextProvider() {
        return Collections.unmodifiableList(identityElementContextProviders);
    }


}
