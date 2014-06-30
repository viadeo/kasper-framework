package com.viadeo.kasper.security.authz.events.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Role has been deleted from User", action = "deleted")
public class AuthorizationUserRemovedFromGroupEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public AuthorizationUserRemovedFromGroupEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}