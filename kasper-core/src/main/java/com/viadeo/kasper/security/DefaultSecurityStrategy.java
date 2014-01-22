// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultSecurityStrategy implements SecurityStrategy {
    private final SecurityConfiguration securityConfiguration;

    // ------------------------------------------------------------------------

    public DefaultSecurityStrategy(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    public void beforeRequest(final Context context) {
        securityConfiguration.getSecurityTokenValidator().validate(context.getSecurityToken());
        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        // TODO: deal with ids decryption using securityConfiguration.getLegacyIdsCipher
    }

    public void afterRequest() {
        // TODO: deal with ids encryption using securityConfiguration.getLegacyIdsCipher
    }

}
