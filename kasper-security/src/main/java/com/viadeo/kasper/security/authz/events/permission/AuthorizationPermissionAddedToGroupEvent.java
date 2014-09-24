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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class AuthorizationPermissionAddedToGroupEvent extends EntityCreatedEvent<Authorization> {

    private Group group;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    public AuthorizationPermissionAddedToGroupEvent(final KasperID entityId, final Group group, final WildcardPermission permission) {
        super(entityId);
        this.group = group;
        this.permission = permission;
    }

    // ------------------------------------------------------------------------

    public Group getGroup() {
        return group;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

}
