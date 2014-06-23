package com.viadeo.kasper.security.authz.events.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Permission has been deleted from Group", action = "deleted")
public class PermissionRemovedFromGroupEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public PermissionRemovedFromGroupEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}