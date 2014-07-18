package com.viadeo.kasper.security.authz.commands.handlers.role;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.AddRoleToGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Role;

@XKasperCommandHandler(domain = Authorization.class, description = "Add a role to a group for authorizations")
public class AddRoleToGroupCommandHandler extends EntityCommandHandler<AddRoleToGroupCommand, Group_has_Role> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<AddRoleToGroupCommand> message) throws Exception {
        Group_has_Role groupHasRole = new Group_has_Role(
                this.getGroup(message.getCommand().getGroupId()),
                this.getRole(message.getCommand().getRoleId())
        );
        this.getRepository().add(groupHasRole);
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

    public Group getGroup(final KasperID id) {
        Group group = null;
        final Optional<ClientRepository<Group>> groupRepositoryOpt = this.getRepositoryOf(Group.class);
        if (groupRepositoryOpt.isPresent()) {
            group = groupRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return group;
    }
}
