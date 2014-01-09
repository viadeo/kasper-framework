// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.IdentityContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;

public abstract class BaseSecurityInterceptor {

    private final IdentityContextProvider identityContextProvider;

    // ------------------------------------------------------------------------

    public BaseSecurityInterceptor(final SecurityConfiguration securityConfiguration) {
        this.identityContextProvider = securityConfiguration.getIdentityContextProvider();
    }

    protected void addSecurityIdentity(final Context context) throws KasperSecurityException {
        identityContextProvider.provideIdentity(context);
    }

}
