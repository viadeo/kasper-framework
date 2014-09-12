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
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Permission;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a permission from a group for authorizations")
public class RemovePermissionFromGroupCommandHandler extends EntityCommandHandler<RemovePermissionFromGroupCommand, Group_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemovePermissionFromGroupCommand> message) throws Exception {
        final Optional<WildcardPermission> permission = this.getPermission(message.getCommand().getPermissionId());
        final Optional<Group> group = this.getGroup(message.getCommand().getGroupId());
        if (permission.isPresent() && group.isPresent()) {
            final Optional<Group_has_Permission> groupHasPermission = this.getRepository().load(new DefaultKasperRelationId(group.get().getEntityId(), permission.get().getEntityId()));
            if(groupHasPermission.isPresent()) {
                groupHasPermission.get().delete();
                return CommandResponse.ok();
            }else{
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
