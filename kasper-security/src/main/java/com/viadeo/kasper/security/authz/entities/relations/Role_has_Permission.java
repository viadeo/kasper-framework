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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.RolePermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionAddedToRoleEvent;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionRemovedFromRoleEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class Role_has_Permission extends Relation<Role, WildcardPermission> {

    private Role role;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    protected Role_has_Permission() {  }

    public Role_has_Permission(final Role role, final WildcardPermission permission) {
        RolePermissionAssociationId rolePermissionAssociationId = new RolePermissionAssociationId(role.getEntityId(), permission.getEntityId());
        apply(new AuthorizationPermissionAddedToRoleEvent(rolePermissionAssociationId, role, permission));
    }

    @EventHandler
    public void onCreated(final AuthorizationPermissionAddedToRoleEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.role = event.getRole();
        this.permission = event.getPermission();
    }

    // ------------------------------------------------------------------------

    public Role_has_Permission delete() {
        apply(new AuthorizationPermissionRemovedFromRoleEvent(getEntityId()));
        return this;
    }

    @EventHandler
    public void onDeleted(final AuthorizationPermissionRemovedFromRoleEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public Role getRole() {
        return role;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPermission(WildcardPermission permission) {
        this.permission = permission;
    }

    public static final Role_has_Permission build(final Role role, final WildcardPermission permission){
        RolePermissionAssociationId rolePermissionAssociationId = new RolePermissionAssociationId(role.getEntityId(), permission.getEntityId());
        Role_has_Permission role_has_permission = new Role_has_Permission();
        role_has_permission.setId(rolePermissionAssociationId);
        role_has_permission.setPermission(permission);
        role_has_permission.setRole(role);
        return role_has_permission;
    }

}
