package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserRoleAssociationId;
import com.viadeo.kasper.security.authz.events.role.RoleAddedToUserEvent;
import com.viadeo.kasper.security.authz.events.role.RoleRemovedFromUserEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class User_has_Role extends Relation<User, Role> {

    private User user;
    private Role role;

    public User_has_Role(final User user, final Role role) {
        apply(new RoleAddedToUserEvent(new UserRoleAssociationId(user.getEntityId(), role.getEntityId()), user, role));
    }

    @EventHandler
    public void onCreated(final RoleAddedToUserEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.user = event.getUser();
        this.role = event.getRole();
    }

    public User_has_Role delete() {
        apply(new RoleRemovedFromUserEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final RoleRemovedFromUserEvent event) {
        this.markDeleted();
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }
}
