// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.group;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperEvent(description = "An Authorization group has been created", action = "created")
public class AuthorizationGroupCreatedEvent extends EntityCreatedEvent<Authorization> {

    private String name;
    private List<User> users;
    private List<Role> roles;
    private List<WildcardPermission> permissions;

    // ------------------------------------------------------------------------

    public AuthorizationGroupCreatedEvent(final KasperID entityId, final String name) {
        super(checkNotNull(entityId));
        this.name = checkNotNull(name);
        this.users = Lists.newArrayList();
        this.roles = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
    }

    public AuthorizationGroupCreatedEvent(final KasperID entityId, final String name, final List<User> users, final List<Role> roles, final List<WildcardPermission> permissions) {
        super(entityId);
        this.name = checkNotNull(name);
        this.users = checkNotNull(users);
        this.roles = checkNotNull(roles);
        this.permissions = checkNotNull(permissions);
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<WildcardPermission> getPermissions() {
        return permissions;
    }

}
