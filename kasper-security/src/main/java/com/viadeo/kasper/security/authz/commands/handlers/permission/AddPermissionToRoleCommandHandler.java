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
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.AddPermissionToRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.Role_has_Permission;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Add a permission to a role for authorizations")
public class AddPermissionToRoleCommandHandler extends EntityCommandHandler<AddPermissionToRoleCommand, Role_has_Permission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<AddPermissionToRoleCommand> message) throws Exception {
        final Optional<WildcardPermission> permission = this.getPermission(message.getCommand().getPermissionId());
        final Optional<Role> role = this.getRole(message.getCommand().getRoleId());
        if (role.isPresent() && permission.isPresent()) {
            final Role_has_Permission role_has_permission = new Role_has_Permission(role.get(), permission.get());
            this.getRepository().add(role_has_permission);
            return CommandResponse.ok();
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

    public Optional<Role> getRole(final KasperID id) {
        final Optional<ClientRepository<Role>> roleRepositoryOpt = this.getRepositoryOf(Role.class);
        if (roleRepositoryOpt.isPresent()) {
            try {
                return Optional.of(roleRepositoryOpt.get().business().get(id));
            } catch (AggregateNotFoundException e) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

}
