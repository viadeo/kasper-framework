// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class PermissionAddedToUserEvent extends EntityCreatedEvent<Authorization> {

    private User user;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    public PermissionAddedToUserEvent(final KasperID entityId, final User user, final WildcardPermission permission) {
        super(entityId);
        this.user = user;
        this.permission = permission;
    }

    // ------------------------------------------------------------------------

    public User getUser() {
        return user;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

}
