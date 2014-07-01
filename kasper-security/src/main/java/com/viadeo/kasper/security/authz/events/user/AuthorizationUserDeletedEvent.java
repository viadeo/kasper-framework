package com.viadeo.kasper.security.authz.events.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization User has been deleted", action = "deleted")
public class AuthorizationUserDeletedEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public AuthorizationUserDeletedEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}