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
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionAddedToUserEvent;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionRemovedFromUserEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class User_has_Permission extends Relation<User, WildcardPermission> {

    private User user;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    protected User_has_Permission() { }

    public User_has_Permission(final User user, final WildcardPermission permission) {
        UserPermissionAssociationId userPermissionAssociationId = new UserPermissionAssociationId(user.getEntityId(), permission.getEntityId());
        apply(new AuthorizationPermissionAddedToUserEvent(userPermissionAssociationId, user, permission));
    }

    @EventHandler
    public void onCreated(final AuthorizationPermissionAddedToUserEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.user = event.getUser();
        this.permission = event.getPermission();
    }

    // ------------------------------------------------------------------------

    public User_has_Permission delete() {
        apply(new AuthorizationPermissionRemovedFromUserEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final AuthorizationPermissionRemovedFromUserEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public User getUser() {
        return user;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPermission(WildcardPermission permission) {
        this.permission = permission;
    }

    public static final User_has_Permission build(final User user, final WildcardPermission permission){
        UserPermissionAssociationId userPermissionAssociationId = new UserPermissionAssociationId(user.getEntityId(), permission.getEntityId());
        User_has_Permission user_has_permission = new User_has_Permission();
        user_has_permission.setId(userPermissionAssociationId);
        user_has_permission.setPermission(permission);
        user_has_permission.setUser(user);
        return user_has_permission;
    }
}
