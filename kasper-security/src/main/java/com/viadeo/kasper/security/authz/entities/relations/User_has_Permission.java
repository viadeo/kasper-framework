package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.UserPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.PermissionAddedToUserEvent;
import com.viadeo.kasper.security.authz.events.permission.PermissionRemovedFromUserEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class User_has_Permission extends Relation<User, WildcardPermission> {

    private User user;
    private WildcardPermission permission;

    public User_has_Permission(final User user, final WildcardPermission permission) {
        apply(new PermissionAddedToUserEvent(new UserPermissionAssociationId(user.getEntityId(), permission.getEntityId()), user, permission));
    }

    @EventHandler
    public void onCreated(final PermissionAddedToUserEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.user = event.getUser();
        this.permission = event.getPermission();
    }

    public User_has_Permission delete() {
        apply(new PermissionRemovedFromUserEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final PermissionRemovedFromUserEvent event) {
        this.markDeleted();
    }

    public User getUser() {
        return user;
    }

    public WildcardPermission getPermission() {
        return permission;
    }
}
