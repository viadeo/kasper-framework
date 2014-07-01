package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Role has been deleted from User", action = "deleted")
public class RoleRemovedFromUserEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public RoleRemovedFromUserEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}