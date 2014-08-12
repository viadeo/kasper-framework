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
import com.viadeo.kasper.security.authz.events.role.RoleAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.role.RoleRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "Group_has_Role")
public class Group_has_Role extends Relation<Group, Role> {

    private Group group;
    private Role role;

    // ------------------------------------------------------------------------

    public Group_has_Role() { }

    public Group_has_Role(final Group group, final Role role) {
        apply(new RoleAddedToGroupEvent(new GroupRoleAssociationId(group.getEntityId(), role.getEntityId()), group, role));
    }

    @EventHandler
    public void onCreated(final RoleAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.role = event.getRole();
    }

    // ------------------------------------------------------------------------

    public Group_has_Role delete() {
        apply(new RoleRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final RoleRemovedFromGroupEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public Group getGroup() {
        return group;
    }

    public Role getRole() {
        return role;
    }

}
