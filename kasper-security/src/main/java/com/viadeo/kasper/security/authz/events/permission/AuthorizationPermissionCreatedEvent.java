// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.security.authz.Authorization;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperEvent(description = "An Authorization permission has been created", action = "created")
public class AuthorizationPermissionCreatedEvent extends EntityCreatedEvent<Authorization> {

    private String wildcardString;
    private boolean caseSensitive;

    // ------------------------------------------------------------------------

    public AuthorizationPermissionCreatedEvent(KasperID entityId) {
        super(checkNotNull(entityId));
    }

    public AuthorizationPermissionCreatedEvent(KasperID entityId, String wildcardString, boolean caseSensitive) {
        super(checkNotNull(entityId));
        this.wildcardString = checkNotNull(wildcardString);
        this.caseSensitive = checkNotNull(caseSensitive);
    }

    // ------------------------------------------------------------------------

    public String getWildcardString() {
        return wildcardString;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

}

