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
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.permission.AuthorizationPermissionRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "Group_has_Permission")
public class Group_has_Permission extends Relation<Group, WildcardPermission> {

    private Group group;
    private WildcardPermission permission;

    // ------------------------------------------------------------------------

    protected Group_has_Permission() { }

    public Group_has_Permission(final Group group, final WildcardPermission permission) {
        GroupPermissionAssociationId groupPermissionAssociationId = new GroupPermissionAssociationId(group.getEntityId(), permission.getEntityId());
        apply(new AuthorizationPermissionAddedToGroupEvent(groupPermissionAssociationId, group, permission));
    }

    @EventHandler
    public void onCreated(final AuthorizationPermissionAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.permission = event.getPermission();
    }

    // ------------------------------------------------------------------------

    public Group_has_Permission delete() {
        apply(new AuthorizationPermissionRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final AuthorizationPermissionRemovedFromGroupEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public Group getGroup() {
        return group;
    }

    public WildcardPermission getPermission() {
        return permission;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setPermission(WildcardPermission permission) {
        this.permission = permission;
    }

    public static final Group_has_Permission build(final Group group, final WildcardPermission permission){
        GroupPermissionAssociationId groupPermissionAssociationId = new GroupPermissionAssociationId(group.getEntityId(), permission.getEntityId());
        Group_has_Permission group_has_permission = new Group_has_Permission();
        group_has_permission.setId(groupPermissionAssociationId);
        group_has_permission.setPermission(permission);
        group_has_permission.setGroup(group);
        return group_has_permission;
    }

}
