// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.configuration.impl;

import com.viadeo.kasper.security.callback.*;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperSecurityConfiguration implements SecurityConfiguration {

    private final SecurityTokenValidator securityTokenValidator;
    private final IdentityContextProvider identityContextProviders;
    private final ApplicationIdValidator applicationIdValidator;
    private final IpAddressValidator ipAddressValidator;
    private final AuthorizationValidator authorizationValidator;

    // ------------------------------------------------------------------------

    public KasperSecurityConfiguration(final SecurityTokenValidator securityTokenValidator,
                                       final IdentityContextProvider identityContextProviders,
                                       final ApplicationIdValidator applicationIdValidator,
                                       final IpAddressValidator ipAddressValidator,
                                       final AuthorizationValidator authorizationValidator) {
        this.securityTokenValidator = checkNotNull(securityTokenValidator);
        this.identityContextProviders = checkNotNull(identityContextProviders);
        this.applicationIdValidator = checkNotNull(applicationIdValidator);
        this.ipAddressValidator = checkNotNull(ipAddressValidator);
        this.authorizationValidator = checkNotNull(authorizationValidator);
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

    @Override
    public AuthorizationValidator getAuthorizationValidator() {
        return authorizationValidator;
    }

}
