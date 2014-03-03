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

    ApplicationIdValidator getApplicationIdValidator();

    IpAddressValidator getIpAddressValidator();

    // ------------------------------------------------------------------------

    class Builder {

        private SecurityTokenValidator securityTokenValidator = new DefautSecurityTokenValidator();
        private IdentityContextProvider identityContextProvider = new DefaultIdentityContextProvider();
        private ApplicationIdValidator applicationIdValidator = new DefaultApplicationIdValidator();
        private IpAddressValidator ipAddressValidator = new DefaultIpAddressValidator();

        // --------------------------------------------------------------------

        public Builder() { }

        public Builder withSecurityTokenValidator(final SecurityTokenValidator securityTokenValidator) {
            checkNotNull(securityTokenValidator);
            this.securityTokenValidator = securityTokenValidator;
            return this;
        }

        // --------------------------------------------------------------------

        public Builder withIdentityProvider(final IdentityContextProvider identityContextProvider) {
            this.identityContextProvider = checkNotNull(identityContextProvider);
            return this;
        }

        public Builder withApplicationIdValidator(final ApplicationIdValidator applicationIdValidator) {
            this.applicationIdValidator = checkNotNull(applicationIdValidator);
            return this;
        }

        public Builder withIpAddressValidator(final IpAddressValidator ipAddressValidator) {
            this.ipAddressValidator = checkNotNull(ipAddressValidator);
            return this;
        }

        public SecurityConfiguration build() {
            SecurityConfiguration securityConfiguration = new KasperSecurityConfiguration(
                    securityTokenValidator,
                    identityContextProvider,
                    applicationIdValidator,
                    ipAddressValidator
            );
            return securityConfiguration;
        }

    }

    // ------------------------------------------------------------------------

    class DefautSecurityTokenValidator implements SecurityTokenValidator {
        @Override
        public void validate(String securityToken)
                throws KasperMissingSecurityTokenException,
                       KasperInvalidSecurityTokenException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultIdentityContextProvider implements IdentityContextProvider {
        @Override
        public void provideIdentity(Context context) throws KasperSecurityException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultApplicationIdValidator implements ApplicationIdValidator {
        @Override
        public void validate(String applicationId)
                throws KasperMissingApplicationIdException,
                       KasperInvalidApplicationIdException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultIpAddressValidator implements IpAddressValidator {
        @Override
        public void validate(String ipAddress)
                throws KasperMissingIpAddressException,
                       KasperInvalidIpAddressException {
            /* do nothing */
        }
    }

}
