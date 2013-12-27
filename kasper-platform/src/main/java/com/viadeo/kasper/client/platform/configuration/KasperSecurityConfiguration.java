package com.viadeo.kasper.client.platform.configuration;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.IdentityElementContextProvider;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration {

    private final List<IdentityElementContextProvider> identityElementContextProviders = Lists.newArrayList();

    public void addIdentityElementContextProvider(IdentityElementContextProvider provider) {
        checkNotNull(provider);
        identityElementContextProviders.add(provider);
    }

    public List<IdentityElementContextProvider> getIdentityElementContextProvider() {
        return Collections.unmodifiableList(identityElementContextProviders);
    }


}
