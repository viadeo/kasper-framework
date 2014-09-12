// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Permission has been deleted from User", action = "deleted")
public class AuthorizationPermissionRemovedFromUserEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    // ------------------------------------------------------------------------

    public AuthorizationPermissionRemovedFromUserEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}
