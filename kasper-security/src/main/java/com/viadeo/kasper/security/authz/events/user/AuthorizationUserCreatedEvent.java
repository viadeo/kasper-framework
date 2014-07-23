// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.user;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperEvent(description = "An Authorization user has been created", action = "created")
public class AuthorizationUserCreatedEvent extends EntityCreatedEvent<Authorization> {

    private List<Role> roles;
    private List<WildcardPermission> permissions;

    // ------------------------------------------------------------------------

    public AuthorizationUserCreatedEvent(KasperID entityId) {
        super(checkNotNull(entityId));
        this.roles = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
    }

    public AuthorizationUserCreatedEvent(KasperID entityId, List<Role> roles, List<WildcardPermission> permissions) {
        super(entityId);
        this.roles = checkNotNull(roles);
        this.permissions = checkNotNull(permissions);
    }

    // ------------------------------------------------------------------------

    public List<Role> getRoles() {
        return roles;
    }

    public List<WildcardPermission> getPermissions() {
        return permissions;
    }
    
}
