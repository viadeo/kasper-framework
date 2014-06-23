package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Role has been deleted", action = "deleted")
public class RoleDeletedEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public RoleDeletedEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}
