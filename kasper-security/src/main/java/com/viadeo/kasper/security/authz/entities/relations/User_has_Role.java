// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserRoleAssociationId;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleAddedToUserEvent;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleRemovedFromUserEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class User_has_Role extends Relation<User, Role> {

    private User user;
    private Role role;

    protected User_has_Role() {
    }
    // ------------------------------------------------------------------------

    public User_has_Role(final User user, final Role role) {
        UserRoleAssociationId userRoleAssociationId = new UserRoleAssociationId(user.getEntityId(), role.getEntityId());
        apply(new AuthorizationRoleAddedToUserEvent(userRoleAssociationId, user, role));
    }

    @EventHandler
    public void onCreated(final AuthorizationRoleAddedToUserEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.user = event.getUser();
        this.role = event.getRole();
    }

    // ------------------------------------------------------------------------

    public User_has_Role delete() {
        apply(new AuthorizationRoleRemovedFromUserEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final AuthorizationRoleRemovedFromUserEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static final User_has_Role build(final User user, final Role role){
        UserRoleAssociationId userRoleAssociationId = new UserRoleAssociationId(user.getEntityId(), role.getEntityId());
        User_has_Role user_has_role = new User_has_Role();
        user_has_role.setId(userRoleAssociationId);
        user_has_role.setRole(role);
        user_has_role.setUser(user);
        return user_has_role;
    }

}
