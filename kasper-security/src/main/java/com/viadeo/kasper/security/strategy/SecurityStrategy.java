// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.strategy;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import org.apache.log4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

public class SecurityStrategy {

    protected Context context;
    protected Class<?> clazz;

    // ------------------------------------------------------------------------

    public SecurityStrategy(final Class<?> clazz) {
        this.clazz = checkNotNull(clazz);
    }

    // ------------------------------------------------------------------------

    @Override
    public void beforeRequest(final Context context) throws KasperSecurityException {
        this.context = checkNotNull(context);

        if (!this.clazz.isAnnotationPresent(XKasperPublic.class)) {
            securityConfiguration.getSecurityTokenValidator().validate(context.getSecurityToken());
        }
        securityConfiguration.getIdentityContextProvider().provideIdentity(context);
        securityConfiguration.getApplicationIdValidator().validate(context.getApplicationId());
        securityConfiguration.getIpAddressValidator().validate(context.getIpAddress());
        securityConfiguration.getAuthorizationValidator().validate(context, this.clazz);
    }

    @Override
    public void afterRequest() {
        if (null != context) {
            MDC.put(Context.IP_ADDRESS_SHORTNAME, context.getIpAddress());
            MDC.put(Context.UCOUNTRY_SHORTNAME, context.getUserCountry());
            MDC.put(Context.ULANG_SHORTNAME, context.getUserLang());
            MDC.put(Context.UID_SHORTNAME, context.getUserId());
            MDC.put(Context.APPLICATION_ID_SHORTNAME, context.getApplicationId());
            MDC.put(Context.SECURITY_TOKEN_SHORTNAME, context.getSecurityToken());
        }
    }

}
