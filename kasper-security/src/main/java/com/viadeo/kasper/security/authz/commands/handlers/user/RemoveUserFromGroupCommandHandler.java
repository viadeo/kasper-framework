// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.user;

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
import com.viadeo.kasper.security.authz.commands.user.RemoveUserFromGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Role;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a user from a group for authorizations")
public class RemoveUserFromGroupCommandHandler extends EntityCommandHandler<RemoveUserFromGroupCommand, Group_has_User> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemoveUserFromGroupCommand> message) throws Exception {
        final Optional<User> user = this.getUser(message.getCommand().getUserId());
        final Optional<Group> group = this.getGroup(message.getCommand().getGroupId());
        if (user.isPresent() && group.isPresent()) {
            final Optional<Group_has_User> groupHasUser = this.getRepository().load(new DefaultKasperRelationId(group.get().getEntityId(), user.get().getEntityId()));
            if(groupHasUser.isPresent()) {
                groupHasUser.get().delete();
                return CommandResponse.ok();
            }else{
                return CommandResponse.error(CoreReasonCode.NOT_FOUND);
            }
        } else {
            return CommandResponse.error(CoreReasonCode.INVALID_INPUT);
        }
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

    public Optional<Group> getGroup(final KasperID id) {
        final Optional<ClientRepository<Group>> groupRepositoryOpt = this.getRepositoryOf(Group.class);
        if (groupRepositoryOpt.isPresent()) {
            try {
                return Optional.of(groupRepositoryOpt.get().business().get(id));
            } catch (AggregateNotFoundException e) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

}
