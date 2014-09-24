// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class AuthorizationRoleAddedToUserEvent extends EntityCreatedEvent<Authorization> {

    private User user;
    private Role role;

    // ------------------------------------------------------------------------

    public AuthorizationRoleAddedToUserEvent(final KasperID entityId, final User user, final Role role) {
        super(entityId);
        this.user = user;
        this.role = role;
    }

    // ------------------------------------------------------------------------

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

}
