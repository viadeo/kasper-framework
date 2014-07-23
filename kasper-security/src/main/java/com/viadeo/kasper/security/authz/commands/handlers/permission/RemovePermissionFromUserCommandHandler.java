// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a permission from a user for authorizations")
public class RemovePermissionFromUserCommandHandler extends EntityCommandHandler<RemovePermissionFromUserCommand, User_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemovePermissionFromUserCommand> message) throws Exception {
        final User_has_Permission userHasPermission = new User_has_Permission(
                this.getUser(message.getCommand().getUserId()),
                this.getPermission(message.getCommand().getPermissionId())
        );
        userHasPermission.delete();
        return CommandResponse.ok();
    }

    public WildcardPermission getPermission(final KasperID id) {
        WildcardPermission permission = null;
        final Optional<ClientRepository<WildcardPermission>> permissionRepositoryOpt = this.getRepositoryOf(WildcardPermission.class);
        if (permissionRepositoryOpt.isPresent()) {
            permission = permissionRepositoryOpt.get().business().get(id);
        }
        return permission;
    }

    public User getUser(final KasperID id) {
        User user = null;
        final Optional<ClientRepository<User>> userRepositoryOpt = this.getRepositoryOf(User.class);
        if (userRepositoryOpt.isPresent()) {
            user = userRepositoryOpt.get().business().get(id);
        }
        return user;
    }

}
