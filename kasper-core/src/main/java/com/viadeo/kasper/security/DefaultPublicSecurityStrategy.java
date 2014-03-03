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
        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        securityConfiguration.getApplicationIdValidator().validate(context.getApplicationId());
        securityConfiguration.getIpAddressValidator().validate(context.getIpAddress());
    }

    public void afterRequest() {
    }

}
