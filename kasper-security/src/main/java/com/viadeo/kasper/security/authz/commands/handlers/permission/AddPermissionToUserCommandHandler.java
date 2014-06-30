package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.AddPermissionToUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;

@XKasperCommandHandler(domain = Authorization.class, description = "Add a permission to a user for authorizations")
public class AddPermissionToUserCommandHandler extends EntityCommandHandler<AddPermissionToUserCommand, User_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<AddPermissionToUserCommand> message) throws Exception {
        User_has_Permission user_has_permission = new User_has_Permission(
                this.getUser(message.getCommand().getUserId()),
                this.getPermission(message.getCommand().getPermissionId())
        );
        this.getRepository().add(user_has_permission);
        return CommandResponse.ok();
    }

    public WildcardPermission getPermission(final KasperID id) {
        WildcardPermission permission = null;
        final Optional<ClientRepository<WildcardPermission>> permissionRepositoryOpt = this.getRepositoryOf(WildcardPermission.class);
        if (permissionRepositoryOpt.isPresent()) {
            permission = permissionRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return permission;
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
