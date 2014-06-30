package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupPermissionAssociationId;
import com.viadeo.kasper.security.authz.events.permission.PermissionAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.permission.PermissionRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "Group_has_Permission")
public class Group_has_Permission extends Relation<Group, WildcardPermission> {

    private Group group;
    private WildcardPermission permission;

    public Group_has_Permission(final Group group, final WildcardPermission permission) {
        apply(new PermissionAddedToGroupEvent(new GroupPermissionAssociationId(group.getEntityId(), permission.getEntityId()), group, permission));
    }

    @EventHandler
    public void onCreated(final PermissionAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.permission = event.getPermission();
    }

    public Group_has_Permission delete() {
        apply(new PermissionRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final PermissionAddedToGroupEvent event) {
        this.markDeleted();
    }

    public Group getGroup() {
        return group;
    }

    public WildcardPermission getPermission() {
        return permission;
    }
}
