package com.viadeo.kasper.security.authz.commands.handlers.user;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.RemoveUserFromGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a user from a group for authorizations")
public class RemoveUserFromGroupCommandHandler extends EntityCommandHandler<RemoveUserFromGroupCommand, Group_has_User> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemoveUserFromGroupCommand> message) throws Exception {
        Group_has_User groupHasUser = new Group_has_User(
                this.getGroup(message.getCommand().getGroupId()),
                this.getUser(message.getCommand().getUserId())
        );
        this.getRepository().add(groupHasUser.delete());
        return CommandResponse.ok();
    }

    public User getUser(final KasperID id) {
        User user = null;
        final Optional<ClientRepository<User>> userRepositoryOpt = this.getRepositoryOf(User.class);
        if (userRepositoryOpt.isPresent()) {
            user = userRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return user;
    }

    public Group getGroup(final KasperID id) {
        Group group = null;
        final Optional<ClientRepository<Group>> groupRepositoryOpt = this.getRepositoryOf(Group.class);
        if (groupRepositoryOpt.isPresent()) {
            group = groupRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return group;
    }
}
