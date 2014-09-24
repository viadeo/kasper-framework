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
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupUserAssociationId;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.user.AuthorizationUserRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class Group_has_User extends Relation<Group, User> {

    private Group group;
    private User user;

    // ------------------------------------------------------------------------

    public Group_has_User() { }

    public Group_has_User(final Group group, final User user) {
        GroupUserAssociationId groupUserAssociationId = new GroupUserAssociationId(group.getEntityId(), user.getEntityId());
        apply(new AuthorizationUserAddedToGroupEvent(groupUserAssociationId, group, user));
    }

    @EventHandler
    public void onCreated(final AuthorizationUserAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.user = event.getUser();
    }

    // ------------------------------------------------------------------------

    public Group_has_User delete() {
        apply(new AuthorizationUserRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final AuthorizationUserRemovedFromGroupEvent event) {
        this.markDeleted();
    }

    // ------------------------------------------------------------------------

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static final Group_has_User build(final Group group, final User user){
        GroupUserAssociationId groupUserAssociationId = new GroupUserAssociationId(group.getEntityId(), user.getEntityId());
        Group_has_User group_has_user = new Group_has_User();
        group_has_user.setId(groupUserAssociationId);
        group_has_user.setUser(user);
        group_has_user.setGroup(group);
        return group_has_user;
    }
}
