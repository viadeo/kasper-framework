// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.configuration;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.mgt.impl.DefaultAuthorizationSecurityManager;
import com.viadeo.kasper.security.callback.*;
import com.viadeo.kasper.security.callback.impl.DefaultAuthorizationValidator;
import com.viadeo.kasper.security.configuration.impl.KasperSecurityConfiguration;
import com.viadeo.kasper.security.exception.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This interface allows defining security configuration of the platform.
 */
public interface SecurityConfiguration {

    SecurityTokenValidator getSecurityTokenValidator();

    IdentityContextProvider getIdentityContextProvider();

    ApplicationIdValidator getApplicationIdValidator();

    IpAddressValidator getIpAddressValidator();

    AuthorizationValidator getAuthorizationValidator();

    // ------------------------------------------------------------------------

    class Builder {

        private SecurityTokenValidator securityTokenValidator = new DefautSecurityTokenValidator();
        private IdentityContextProvider identityContextProvider = new DefaultIdentityContextProvider();
        private ApplicationIdValidator applicationIdValidator = new DefaultApplicationIdValidator();
        private IpAddressValidator ipAddressValidator = new DefaultIpAddressValidator();
        private AuthorizationValidator authorizationValidator = new FakeAuthorizationValidator();

        // --------------------------------------------------------------------

        public Builder() {
        }

        public Builder withSecurityTokenValidator(final SecurityTokenValidator securityTokenValidator) {
            this.securityTokenValidator = checkNotNull(securityTokenValidator);
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

        public Builder withAuthorizationValidator(final AuthorizationValidator authorizationValidator) {
            this.authorizationValidator = checkNotNull(authorizationValidator);
            return this;
        }

        public SecurityConfiguration build() {
            final SecurityConfiguration securityConfiguration = new KasperSecurityConfiguration(
                securityTokenValidator,
                identityContextProvider,
                applicationIdValidator,
                ipAddressValidator,
                authorizationValidator
            );
            return securityConfiguration;
        }

    }

    // ------------------------------------------------------------------------

    class DefautSecurityTokenValidator implements SecurityTokenValidator {
        @Override
        public void validate(final String securityToken)
                throws KasperMissingSecurityTokenException,
                       KasperInvalidSecurityTokenException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultIdentityContextProvider implements IdentityContextProvider {
        @Override
        public void provideIdentity(final Context context) throws KasperSecurityException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultApplicationIdValidator implements ApplicationIdValidator {
        @Override
        public void validate(final String applicationId)
                throws KasperMissingApplicationIdException,
                       KasperInvalidApplicationIdException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class DefaultIpAddressValidator implements IpAddressValidator {
        @Override
        public void validate(final String ipAddress)
                throws KasperMissingIpAddressException,
                       KasperInvalidIpAddressException {
            /* do nothing */
        }
    }

    // ------------------------------------------------------------------------

    class FakeAuthorizationValidator implements AuthorizationValidator {
        @Override
        public void validate(Context context, Class<?> clazz) throws KasperUnauthorizedException {
            /* do nothing */
        }
    }

}
