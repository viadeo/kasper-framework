package com.viadeo.kasper.security.authz.events.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class PermissionCreatedEvent extends EntityCreatedEvent<Authorization> {

    private String wildcardString;
    private boolean caseSensitive;

    public PermissionCreatedEvent(KasperID entityId) {
        super(checkNotNull(entityId));
    }

    public PermissionCreatedEvent(KasperID entityId, String wildcardString, boolean caseSensitive) {
        super(checkNotNull(entityId));
        this.wildcardString = checkNotNull(wildcardString);
        this.caseSensitive = checkNotNull(caseSensitive);
    }

    public String getWildcardString() {
        return wildcardString;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}

