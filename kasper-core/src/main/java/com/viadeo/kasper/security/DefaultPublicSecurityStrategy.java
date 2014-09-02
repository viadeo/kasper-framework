// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import org.apache.log4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultPublicSecurityStrategy implements SecurityStrategy {

    protected final SecurityConfiguration securityConfiguration;
    protected Context context;

    // ------------------------------------------------------------------------

    public DefaultPublicSecurityStrategy(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    // ------------------------------------------------------------------------

    @Override
    public void beforeRequest(final Context context) throws KasperSecurityException {
        this.context = checkNotNull(context);

        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        securityConfiguration.getApplicationIdValidator().validate(context.getApplicationId());
        securityConfiguration.getIpAddressValidator().validate(context.getIpAddress());
    }

    @Override
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
