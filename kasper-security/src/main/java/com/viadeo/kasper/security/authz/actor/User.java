// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.actor;

import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class User extends Actor {

    // ------------------------------------------------------------------------

    public User() {
        super();
    }


    public User(final List<Role> roles, final List<Permission> permissions) {
        super( roles, permissions);
    }

}
