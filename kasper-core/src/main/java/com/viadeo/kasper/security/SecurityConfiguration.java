// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.callback.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This interface allows defining security configuration of the platform.
 */

public interface SecurityConfiguration {

    SecurityTokenValidator getSecurityTokenValidator();

    IdentityContextProvider getIdentityContextProvider();

    LegacyIdsCipher getLegacyIdsCipher();

    ApplicationIdValidator getApplicationIdValidator();

    IpAddressValidator getIpAddressValidator();


    class Builder {
        private SecurityTokenValidator securityTokenValidator = new DefautSecurityTokenValidator();
        private IdentityContextProvider identityContextProvider = new DefaultIdentityContextProvider();
        private LegacyIdsCipher legacyIdsCipher = new DefaultLegacyIdsCipher();
        private ApplicationIdValidator applicationIdValidator = new DefaultApplicationIdValidator();
        private IpAddressValidator ipAddressValidator = new DefaultIpAddressValidator();

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

        public Builder withApplicationIdValidator(final ApplicationIdValidator applicationIdValidator) {
            checkNotNull(applicationIdValidator);
            this.applicationIdValidator = applicationIdValidator;
            return this;
        }

        public Builder withIpAddressValidator(final IpAddressValidator ipAddressValidator) {
            checkNotNull(ipAddressValidator);
            this.ipAddressValidator = ipAddressValidator;
            return this;
        }

        public SecurityConfiguration build() {
            SecurityConfiguration securityConfiguration = new KasperSecurityConfiguration(
                    securityTokenValidator,
                    identityContextProvider,
                    legacyIdsCipher,
                    applicationIdValidator,
                    ipAddressValidator
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

    class DefaultApplicationIdValidator implements ApplicationIdValidator {

        @Override
        public void validate(String applicationId) throws KasperMissingApplicationIdException, KasperInvalidApplicationIdException {
        }
    }

    class DefaultIpAddressValidator implements IpAddressValidator {

        @Override
        public void validate(String ipAddress) throws KasperMissingIpAddressException, KasperInvalidIpAddressException {
        }
    }
}
