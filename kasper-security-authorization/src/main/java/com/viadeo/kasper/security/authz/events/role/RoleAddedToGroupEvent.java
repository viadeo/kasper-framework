package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class RoleAddedToGroupEvent extends EntityCreatedEvent<Authorization> {

    private Group group;
    private Role role;

    public RoleAddedToGroupEvent(final KasperID entityId, final Group group, final Role role) {
        super(entityId);
        this.group = group;
        this.role = role;
    }

    public Group getGroup() {
        return group;
    }

    public Role getRole() {
        return role;
    }
}
