package com.viadeo.kasper.security.authz.events.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class UserAddedToGroupEvent extends EntityCreatedEvent<Authorization> {

    private Group group;
    private User user;

    public UserAddedToGroupEvent(final KasperID entityId, final Group group, final User user) {
        super(entityId);
        this.group = group;
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }
}
