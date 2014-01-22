// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.security.callback.IdentityContextProvider;
import com.viadeo.kasper.security.callback.LegacyIdsCipher;
import com.viadeo.kasper.security.callback.SecurityTokenValidator;

import static com.google.common.base.Preconditions.checkNotNull;

class KasperSecurityConfiguration implements SecurityConfiguration {

    private final SecurityTokenValidator securityTokenValidator;
    private final IdentityContextProvider identityContextProviders;
    private final LegacyIdsCipher legacyIdsCipher;


    // ------------------------------------------------------------------------

    public KasperSecurityConfiguration(final SecurityTokenValidator securityTokenValidator,
                                       final IdentityContextProvider identityContextProviders,
                                       final LegacyIdsCipher legacyIdsCipher) {
        this.securityTokenValidator = checkNotNull(securityTokenValidator);
        this.identityContextProviders = checkNotNull(identityContextProviders);
        this.legacyIdsCipher = checkNotNull(legacyIdsCipher);
    }

    @Override
    public SecurityTokenValidator getSecurityTokenValidator() {
        return securityTokenValidator;
    }

    @Override
    public IdentityContextProvider getIdentityContextProvider() {
        return identityContextProviders;
    }

    @Override
    public LegacyIdsCipher getLegacyIdsCipher() {
        return legacyIdsCipher;
    }

}
