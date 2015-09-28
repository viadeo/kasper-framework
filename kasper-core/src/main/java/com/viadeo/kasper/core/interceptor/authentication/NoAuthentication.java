// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.ID;

public class NoAuthentication implements Authenticator {

    @Override
    public boolean isAuthenticated(Context context) {
        return true;
    }

    @Override
    public Optional<ID> getSubject(Context context) {
        return Optional.absent();
    }

}
