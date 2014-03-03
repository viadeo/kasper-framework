// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultPublicSecurityStrategy implements SecurityStrategy {

    private final SecurityConfiguration securityConfiguration;

    // ------------------------------------------------------------------------

    public DefaultPublicSecurityStrategy(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    // ------------------------------------------------------------------------

    public void beforeRequest(final Context context) {
        checkNotNull(context);
        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        securityConfiguration.getApplicationIdValidator().validate(context.getApplicationId());
        securityConfiguration.getIpAddressValidator().validate(context.getIpAddress());
    }

    public void afterRequest() {
        /* do nothing */
    }

}
