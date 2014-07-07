package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.RolePermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.PermissionAddedToRoleEvent;
import com.viadeo.kasper.security.authz.events.permission.PermissionRemovedFromRoleEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class Role_has_Permission extends Relation<Role, WildcardPermission> {

    private Role role;
    private WildcardPermission permission;

    public Role_has_Permission(final Role role, final WildcardPermission permission) {
        apply(new PermissionAddedToRoleEvent(new RolePermissionAssociationId(role.getEntityId(), permission.getEntityId()), role, permission));
    }

    @EventHandler
    public void onCreated(final PermissionAddedToRoleEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.role = event.getRole();
        this.permission = event.getPermission();
    }

    public Role_has_Permission delete() {
        apply(new PermissionRemovedFromRoleEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final PermissionRemovedFromRoleEvent event) {
        this.markDeleted();
    }

    public Role getRole() {
        return role;
    }

    public WildcardPermission getPermission() {
        return permission;
    }
}
