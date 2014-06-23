package com.viadeo.kasper.security.authz.events.user;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

@XKasperEvent(description = "An Authorization user has been created", action = "created")
public class UserCreatedEvent extends EntityCreatedEvent<Authorization> {

    private List<Role> roles;
    private List<Permission> permissions;

    public UserCreatedEvent(KasperID entityId) {
        super(checkNotNull(entityId));
        this.roles = Lists.newArrayList();
        this.permissions = Lists.newArrayList();
    }

    public UserCreatedEvent(KasperID entityId, List<Role> roles, List<Permission> permissions) {
        super(entityId);
        this.roles = checkNotNull(roles);
        this.permissions = checkNotNull(permissions);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
