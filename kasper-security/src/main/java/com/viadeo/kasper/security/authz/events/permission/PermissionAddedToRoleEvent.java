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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class PermissionAddedToRoleEvent extends EntityCreatedEvent<Authorization> {

    private Role role;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    public PermissionAddedToRoleEvent(final KasperID entityId, final Role role, final WildcardPermission permission) {
        super(entityId);
        this.role = role;
        this.permission = permission;
    }

    // ------------------------------------------------------------------------

    public Role getRole() {
        return role;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

}
