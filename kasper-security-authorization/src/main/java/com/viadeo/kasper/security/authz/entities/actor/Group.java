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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.events.group.AuthorizationGroupCreatedEvent;
import com.viadeo.kasper.security.authz.events.group.AuthorizationGroupDeletedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperConcept(domain = Authorization.class, description = "", label = "Group")
public class Group extends Actor {

    private String name;
    private List<User> users;

    // ------------------------------------------------------------------------

    public Group(final String name){
        apply(new AuthorizationGroupCreatedEvent(new DefaultKasperId(), name));
    }

    public Group(final KasperID kasperID, final String name){
        apply(new AuthorizationGroupCreatedEvent(kasperID, name));
    }

    public Group(final String name,
                 final List<Role> roles,
                 final List<WildcardPermission> permissions,
                 final List<User> users) {
        apply(new AuthorizationGroupCreatedEvent(new DefaultKasperId(), name, users, roles, permissions));
    }

    public Group(final KasperID kasperID,
                 final String name,
                 final List<Role> roles,
                 final List<WildcardPermission> permissions,
                 final List<User> users) {
        apply(new AuthorizationGroupCreatedEvent(kasperID, name, users, roles, permissions));
    }

    @EventHandler
    public void onCreated(AuthorizationGroupCreatedEvent event) {
        setId(event.getEntityId());
        setName(event.getName());
        setUsers(event.getUsers());
        setRoles(event.getRoles());
        setPermissions(event.getPermissions());
    }

    public Group delete() {
        apply(new AuthorizationGroupDeletedEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final AuthorizationGroupDeletedEvent e) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = checkNotNull(name);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = checkNotNull(users);
    }

    public void addUser(final User user){
        users.add(checkNotNull(user));
    }

    public void removeUser(final User user){
        users.remove(checkNotNull(user));
    }
}
