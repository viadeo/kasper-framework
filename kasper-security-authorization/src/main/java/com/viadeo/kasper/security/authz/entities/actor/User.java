// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.entities.actor;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.events.user.UserCreatedEvent;
import com.viadeo.kasper.security.authz.events.user.UserDeletedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.util.List;

@XKasperConcept(domain = Authorization.class, description = "", label = "User")
public class User extends Actor {

    // ------------------------------------------------------------------------

    public User() {
        super();
    }

    public User(final KasperID kasperID) {
        apply(new UserCreatedEvent(kasperID));
    }

    public User(final List<Role> roles, final List<Permission> permissions) {
        apply(new UserCreatedEvent(new DefaultKasperId(), roles, permissions));
    }

    public User(final KasperID kasperID, final List<Role> roles, final List<Permission> permissions) {
        apply(new UserCreatedEvent(kasperID, roles, permissions));
    }

    @EventHandler
    public void onCreated(UserCreatedEvent event) {
        setId(event.getEntityId());
        setRoles(event.getRoles());
        setPermissions(event.getPermissions());
    }

    public User delete() {
        apply(new UserDeletedEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final UserDeletedEvent e) {
        this.markDeleted();
    }

}
