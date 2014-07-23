// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.entities.actor;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.annotation.XKasperEntityStoreCreator;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserCreatedEvent;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserDeletedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperConcept(domain = Authorization.class, description = "", label = "User")
public class User extends Actor {

    private String firstName;
    private String lastName;

    // ------------------------------------------------------------------------

    public User() { }

    @XKasperEntityStoreCreator
    public User(final String firstName, final String lastName) {
        super();
        this.firstName = checkNotNull(firstName);
        this.lastName = checkNotNull(lastName);
    }

    public User(final KasperID kasperID, final String firstName, final String lastName) {
        apply(new AuthorizationUserCreatedEvent(kasperID));
        this.firstName = checkNotNull(firstName);
        this.lastName = checkNotNull(lastName);
    }

    public User(final String firstName, final String lastName, final List<Role> roles, final List<WildcardPermission> permissions) {
        apply(new AuthorizationUserCreatedEvent(new DefaultKasperId(), roles, permissions));
        this.firstName = checkNotNull(firstName);
        this.lastName = checkNotNull(lastName);
    }

    public User(final KasperID kasperID, final String firstName, final String lastName, final List<Role> roles, final List<WildcardPermission> permissions) {
        apply(new AuthorizationUserCreatedEvent(kasperID, roles, permissions));
        this.firstName = checkNotNull(firstName);
        this.lastName = checkNotNull(lastName);
    }

    @EventHandler
    public void onCreated(AuthorizationUserCreatedEvent event) {
        setId(event.getEntityId());
        setRoles(event.getRoles());
        setPermissions(event.getPermissions());
    }

    // ------------------------------------------------------------------------

    public User delete() {
        apply(new AuthorizationUserDeletedEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final AuthorizationUserDeletedEvent e) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // ------------------------------------------------------------------------

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof User) {
            final User sr = (User) o;
            return (getEntityId() != null ? getEntityId().equals(sr.getEntityId()) : sr.getEntityId() == null);
        }
        return false;
    }

}
