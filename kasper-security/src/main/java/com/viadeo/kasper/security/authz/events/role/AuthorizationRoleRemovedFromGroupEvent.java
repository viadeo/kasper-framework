// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.events.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.EntityDeletedEvent;
import com.viadeo.kasper.security.authz.Authorization;

@XKasperEvent(description = "An authorization Role has been deleted from Group", action = "deleted")
public class AuthorizationRoleRemovedFromGroupEvent extends EntityDeletedEvent<Authorization> {

    final KasperID entityId;

    // ------------------------------------------------------------------------

    public AuthorizationRoleRemovedFromGroupEvent(final KasperID entityId) {
        this.entityId = entityId;
    }

}
