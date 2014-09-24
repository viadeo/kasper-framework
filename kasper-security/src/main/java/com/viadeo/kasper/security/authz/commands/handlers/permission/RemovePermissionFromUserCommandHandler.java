// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a permission from a user for authorizations")
public class RemovePermissionFromUserCommandHandler extends EntityCommandHandler<RemovePermissionFromUserCommand, User_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemovePermissionFromUserCommand> message) throws Exception {
        final Optional<WildcardPermission> permission = this.getPermission(message.getCommand().getPermissionId());
        final Optional<User> user = this.getUser(message.getCommand().getUserId());
        if (user.isPresent() && permission.isPresent()) {
            final Optional<User_has_Permission> userHasPermission = this.getRepository().load(new DefaultKasperRelationId(user.get().getEntityId(), permission.get().getEntityId()));
            if(userHasPermission.isPresent()) {
                userHasPermission.get().delete();
                return CommandResponse.ok();
            }else {
                return CommandResponse.error(CoreReasonCode.NOT_FOUND);
            }
        } else {
            return CommandResponse.error(CoreReasonCode.INVALID_INPUT);
        }
    }


    public Optional<WildcardPermission> getPermission(final KasperID id) {
        final Optional<ClientRepository<WildcardPermission>> permissionRepositoryOpt = this.getRepositoryOf(WildcardPermission.class);
        if (permissionRepositoryOpt.isPresent()) {
            try {
                return Optional.of(permissionRepositoryOpt.get().business().get(id));
            } catch (AggregateNotFoundException e) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

    public Optional<User> getUser(final KasperID id) {
        final Optional<ClientRepository<User>> userRepositoryOpt = this.getRepositoryOf(User.class);
        if (userRepositoryOpt.isPresent()) {
            try {
                return Optional.of(userRepositoryOpt.get().business().get(id));
            } catch (AggregateNotFoundException e) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

}
