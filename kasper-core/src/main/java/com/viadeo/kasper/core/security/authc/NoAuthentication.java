// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;

public class NoAuthentication implements Authenticator {

    @Override
    public boolean isAuthenticated(Context context) {
        return true;
    }
}
