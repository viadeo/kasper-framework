package com.viadeo.kasper.security.authz.entities.relations;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.relations.ids.GroupUserAssociationId;
import com.viadeo.kasper.security.authz.events.user.UserAddedToGroupEvent;
import com.viadeo.kasper.security.authz.events.user.UserRemovedFromGroupEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperRelation(domain = Authorization.class, label = "")
public class Group_has_User extends Relation<Group, User> {

    private Group group;
    private User user;

    public Group_has_User(final Group group, final User user) {
        apply(new UserAddedToGroupEvent(new GroupUserAssociationId(group.getEntityId(), user.getEntityId()), group, user));
    }

    @EventHandler
    public void onCreated(final UserAddedToGroupEvent event) {
        setId((KasperRelationID) event.getEntityId());
        this.group = event.getGroup();
        this.user = event.getUser();
    }

    public Group_has_User delete() {
        apply(new UserRemovedFromGroupEvent(getEntityId()));
        return this;
    }

    @EventHandler
    protected void onDeleted(final UserAddedToGroupEvent event) {
        this.markDeleted();
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }
}
