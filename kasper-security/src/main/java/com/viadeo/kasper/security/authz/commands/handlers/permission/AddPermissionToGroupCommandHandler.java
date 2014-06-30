package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.AddPermissionToGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Permission;

@XKasperCommandHandler(domain = Authorization.class, description = "Add a permission to a group for authorizations")
public class AddPermissionToGroupCommandHandler extends EntityCommandHandler<AddPermissionToGroupCommand, Group_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<AddPermissionToGroupCommand> message) throws Exception {
        Group_has_Permission groupHasPermission = new Group_has_Permission(
                this.getGroup(message.getCommand().getGroupId()),
                this.getPermission(message.getCommand().getPermissionId())
        );
        this.getRepository().add(groupHasPermission);
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

    public Group getGroup(final KasperID id) {
        Group group = null;
        final Optional<ClientRepository<Group>> groupRepositoryOpt = this.getRepositoryOf(Group.class);
        if (groupRepositoryOpt.isPresent()) {
            group = groupRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return group;
    }
}
