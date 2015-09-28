// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager;
import com.viadeo.kasper.core.interceptor.authorization.CombinesWith;

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
