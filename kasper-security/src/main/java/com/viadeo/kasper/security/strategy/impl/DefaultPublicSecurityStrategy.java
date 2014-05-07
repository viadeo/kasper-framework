// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.strategy.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import org.apache.log4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultPublicSecurityStrategy implements SecurityStrategy {

    protected final SecurityConfiguration securityConfiguration;
    protected Context context;
    protected Class<?> clazz;

    // ------------------------------------------------------------------------

    public DefaultPublicSecurityStrategy(final SecurityConfiguration securityConfiguration, final Class<?> clazz) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
        this.clazz = checkNotNull(clazz);
    }

    // ------------------------------------------------------------------------

    public void beforeRequest(final Context context) {
        this.context = checkNotNull(context);

        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        securityConfiguration.getApplicationIdValidator().validate(context.getApplicationId());
        securityConfiguration.getIpAddressValidator().validate(context.getIpAddress());
        securityConfiguration.getAuthorizationValidator().validate(context, this.clazz);
    }

    public void afterRequest() {
        if (null != context) {
            MDC.put(Context.IP_ADDRESS_SHORTNAME, context.getIpAddress());
            MDC.put(Context.UCOUNTRY_SHORTNAME, context.getUserCountry());
            MDC.put(Context.ULANG_SHORTNAME, context.getUserLang());
            MDC.put(Context.UID_SHORTNAME, context.getUserId());
            MDC.put(Context.APPLICATION_ID_SHORTNAME, context.getApplicationId());
        }
    }

}
