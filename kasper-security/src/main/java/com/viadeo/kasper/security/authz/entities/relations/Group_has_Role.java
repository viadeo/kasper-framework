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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupRoleAssociationId;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.role.AuthorizationRoleRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "Group_has_Role")
public class Group_has_Role extends Relation<Group, Role> {

    private Group group;
    private Role role;

    // ------------------------------------------------------------------------

    protected Group_has_Role() { }

    public Group_has_Role(final Group group, final Role role) {
        GroupRoleAssociationId groupRoleAssociationId = new GroupRoleAssociationId(group.getEntityId(), role.getEntityId());
        apply(new AuthorizationRoleAddedToGroupEvent(groupRoleAssociationId, group, role));
    }

    @EventHandler
    public void onCreated(final AuthorizationRoleAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.role = event.getRole();
    }

    // ------------------------------------------------------------------------

    public Group_has_Role delete() {
        apply(new AuthorizationRoleRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final AuthorizationRoleRemovedFromGroupEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public Group getGroup() {
        return group;
    }

    public Role getRole() {
        return role;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static final Group_has_Role build(final Group group, final Role role){
        GroupRoleAssociationId groupRoleAssociationId = new GroupRoleAssociationId(group.getEntityId(), role.getEntityId());
        Group_has_Role group_has_role = new Group_has_Role();
        group_has_role.setId(groupRoleAssociationId);
        group_has_role.setRole(role);
        group_has_role.setGroup(group);
        return group_has_role;
    }
}
