package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.RemovePermissionFromRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Role_has_Permission;

@XKasperCommandHandler(domain = Authorization.class, description = "Remove a permission from a role for authorizations")
public class RemovePermissionFromRoleCommandHandler extends EntityCommandHandler<RemovePermissionFromRoleCommand, Role_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<RemovePermissionFromRoleCommand> message) throws Exception {
        Role_has_Permission roleHasPermission = new Role_has_Permission(
                this.getRole(message.getCommand().getRoleId()),
                this.getPermission(message.getCommand().getPermissionId())
        );
        this.getRepository().add(roleHasPermission.delete());
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

    public Role getRole(final KasperID id) {
        Role role = null;
        final Optional<ClientRepository<Role>> roleRepositoryOpt = this.getRepositoryOf(Role.class);
        if (roleRepositoryOpt.isPresent()) {
            role = roleRepositoryOpt.get().load(id, Optional.<Long>absent()).get();
        }
        return role;
    }
}
