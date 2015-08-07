// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;

public class OnlyForTestsAuthorizationManagerReturningTrue implements AuthorizationManager {

    @Override
    public boolean isPermitted(String[] permissions, CombinesWith combineWith, ID actorID, Optional targetId) {
        return true;
    }

    @Override
    public boolean hasRole(String[] roles, CombinesWith combineWith, ID actorID, Optional targetId) {
        return true;
    }
}
