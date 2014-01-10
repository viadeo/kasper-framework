// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.security.IdentityContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration implements SecurityConfiguration {

    private final IdentityContextProvider identityContextProviders;

    // ------------------------------------------------------------------------

    public KasperSecurityConfiguration(final IdentityContextProvider identityContextProviders) {
        this.identityContextProviders = checkNotNull(identityContextProviders);
    }

    @Override
    public IdentityContextProvider getIdentityContextProvider() {
        return identityContextProviders;
    }

}
