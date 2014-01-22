// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.callback.IdentityContextProvider;
import com.viadeo.kasper.security.callback.LegacyIdsCipher;
import com.viadeo.kasper.security.callback.SecurityTokenValidator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This interface allows defining security configuration of the platform.
 */

public interface SecurityConfiguration {

    SecurityTokenValidator getSecurityTokenValidator();
    IdentityContextProvider getIdentityContextProvider();
    LegacyIdsCipher getLegacyIdsCipher();


    class Builder {
        private SecurityTokenValidator securityTokenValidator = new DefautSecurityTokenValidator();
        private IdentityContextProvider identityContextProvider = new DefaultIdentityContextProvider();
        private LegacyIdsCipher legacyIdsCipher = new DefaultLegacyIdsCipher();

        public Builder() {
        }

        public Builder withSecurityTokenValidator(final SecurityTokenValidator securityTokenValidator) {
            checkNotNull(securityTokenValidator);
            this.securityTokenValidator = securityTokenValidator;
            return this;
        }

        public Builder withIdentityProvider(final IdentityContextProvider identityContextProvider) {
            checkNotNull(identityContextProvider);
            this.identityContextProvider = identityContextProvider;
            return this;
        }

        public Builder withLegacyIdsCipher(final LegacyIdsCipher legacyIdsCipher) {
            checkNotNull(legacyIdsCipher);
            this.legacyIdsCipher = legacyIdsCipher;
            return this;
        }

        public SecurityConfiguration build() {
            SecurityConfiguration securityConfiguration = new KasperSecurityConfiguration(
                    securityTokenValidator,
                    identityContextProvider,
                    legacyIdsCipher
            );
            return securityConfiguration;
        }
    }

    class DefautSecurityTokenValidator implements SecurityTokenValidator {

        @Override
        public void validate(String securityToken) throws KasperMissingSecurityTokenException, KasperInvalidSecurityTokenException {
        }
    }

    class DefaultIdentityContextProvider implements IdentityContextProvider {

        @Override
        public void provideIdentity(Context context) throws KasperSecurityException {
        }
    }

    class DefaultLegacyIdsCipher implements LegacyIdsCipher {

        @Override
        public int encrypt(int id) {
            return id;
        }

        @Override
        public int decrypt(int id) {
            return id;
        }
    }
}
