// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.strategy.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import org.apache.log4j.MDC;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultSecurityStrategy extends DefaultPublicSecurityStrategy {

    public DefaultSecurityStrategy(final SecurityConfiguration securityConfiguration,
                                   final Class<?> clazz) {
        super(checkNotNull(securityConfiguration), clazz);
    }

    // ------------------------------------------------------------------------

    @Override
    public void beforeRequest(final Context context) throws KasperSecurityException {
        checkNotNull(context);
        securityConfiguration.getSecurityTokenValidator().validate(context.getSecurityToken());
        super.beforeRequest(context);
    }

    @Override
    public void afterRequest() {
        super.afterRequest();
        if (null != this.context) {
            MDC.put(Context.SECURITY_TOKEN_SHORTNAME, this.context.getSecurityToken());
        }
    }

}
