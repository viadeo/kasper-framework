package com.viadeo.kasper.security.authz.events.group;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Group has been deleted", action = "deleted")
public class AuthorizationGroupDeletedEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    public AuthorizationGroupDeletedEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}