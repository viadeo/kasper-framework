package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class RoleAddedToUserEvent extends EntityCreatedEvent<Authorization> {

    private User user;
    private Role role;

    public RoleAddedToUserEvent(final KasperID entityId, final User user, final Role role) {
        super(entityId);
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }
}
