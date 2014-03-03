// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.security.callback.ApplicationIdValidator;
import com.viadeo.kasper.security.callback.IdentityContextProvider;
import com.viadeo.kasper.security.callback.IpAddressValidator;
import com.viadeo.kasper.security.callback.SecurityTokenValidator;

import static com.google.common.base.Preconditions.checkNotNull;

class KasperSecurityConfiguration implements SecurityConfiguration {

    private final SecurityTokenValidator securityTokenValidator;
    private final IdentityContextProvider identityContextProviders;
    private final ApplicationIdValidator applicationIdValidator;
    private final IpAddressValidator ipAddressValidator;

    // ------------------------------------------------------------------------

    public KasperSecurityConfiguration(final SecurityTokenValidator securityTokenValidator,
                                       final IdentityContextProvider identityContextProviders,
                                       final ApplicationIdValidator applicationIdValidator,
                                       final IpAddressValidator ipAddressValidator) {
        this.securityTokenValidator = checkNotNull(securityTokenValidator);
        this.identityContextProviders = checkNotNull(identityContextProviders);
        this.applicationIdValidator = checkNotNull(applicationIdValidator);
        this.ipAddressValidator = checkNotNull(ipAddressValidator);
    }

    // ------------------------------------------------------------------------

    @Override
    public SecurityTokenValidator getSecurityTokenValidator() {
        return securityTokenValidator;
    }

    @Override
    public IdentityContextProvider getIdentityContextProvider() {
        return identityContextProviders;
    }

    @Override
    public ApplicationIdValidator getApplicationIdValidator() {
        return applicationIdValidator;
    }

    @Override
    public IpAddressValidator getIpAddressValidator() {
        return ipAddressValidator;
    }

}
