package com.viadeo.kasper.security.authz.events.role;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class RoleCreatedEvent extends EntityCreatedEvent<Authorization> {

    private String name;
    List<WildcardPermission> permissions;

    public RoleCreatedEvent(KasperID entityId) {
        super(checkNotNull(entityId));
    }

    public RoleCreatedEvent(KasperID entityId, String name) {
        super(entityId);
        this.name = checkNotNull(name);
        this.permissions = Lists.newArrayList();
    }

    public RoleCreatedEvent(KasperID entityId, String name, List<WildcardPermission> permissions) {
        super(entityId);
        this.name = checkNotNull(name);
        this.permissions = checkNotNull(permissions);
    }

    public String getName() {
        return name;
    }

    public List<WildcardPermission> getPermissions() {
        return permissions;
    }
}
