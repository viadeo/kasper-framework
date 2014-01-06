package com.viadeo.kasper.client.platform.configuration;

import com.google.common.collect.Constraint;
import com.google.common.collect.Constraints;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration implements SecurityConfiguration {

    private final List<IdentityElementContextProvider> identityElementContextProviders = Constraints.constrainedList(
            new ArrayList<IdentityElementContextProvider>(), Constraints.notNull());

    public KasperSecurityConfiguration(List<IdentityElementContextProvider> identityElementContextProviders) {
        checkNotNull(identityElementContextProviders);
        this.identityElementContextProviders.addAll(identityElementContextProviders);
    }

    @Override
    public List<IdentityElementContextProvider> getIdentityElementContextProviders() {
        return Collections.unmodifiableList(identityElementContextProviders);
    }
}
