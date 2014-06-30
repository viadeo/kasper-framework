package com.viadeo.kasper.security.authz.commands.handlers.role;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.RemoveRoleFromUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Role;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a permission from a group for authorizations")
public class RemoveRoleFromUserCommandHandler extends EntityCommandHandler<RemoveRoleFromUserCommand, User_has_Role> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemoveRoleFromUserCommand> message) throws Exception {
        User_has_Role userHasRole = new User_has_Role(
                this.getUser(message.getCommand().getUserId()),
                this.getRole(message.getCommand().getRoleId())
        );
        this.getRepository().add(userHasRole.delete());
        return CommandResponse.ok();
    }

    public Role getRole(final KasperID id) {
        Role role = null;
        final Optional<ClientRepository<Role>> roleRepositoryOpt = this.getRepositoryOf(Role.class);
        if (roleRepositoryOpt.isPresent()) {
            role = roleRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return role;
    }

    public User getUser(final KasperID id) {
        User user = null;
        final Optional<ClientRepository<User>> userRepositoryOpt = this.getRepositoryOf(User.class);
        if (userRepositoryOpt.isPresent()) {
            user = userRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return user;
    }
}
