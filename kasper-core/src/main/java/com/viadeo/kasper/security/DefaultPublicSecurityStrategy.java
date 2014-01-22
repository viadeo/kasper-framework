package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultPublicSecurityStrategy implements SecurityStrategy {
    private final SecurityConfiguration securityConfiguration;

    // ------------------------------------------------------------------------
    public DefaultPublicSecurityStrategy(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    public void beforeRequest(final Context context) {
        // TODO: deal with ids decryption using securityConfiguration.getLegacyIdsCipher
    }

    public void afterRequest() {
        // TODO: deal with ids encryption using securityConfiguration.getLegacyIdsCipher
    }

}
