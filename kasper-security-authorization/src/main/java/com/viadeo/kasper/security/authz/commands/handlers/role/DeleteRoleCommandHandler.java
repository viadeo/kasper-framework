package com.viadeo.kasper.security.authz.commands.handlers.role;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.DeleteRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

@XKasperCommandHandler(domain = Authorization.class, description = "Delete a role for authorizations")
public class DeleteRoleCommandHandler extends EntityCommandHandler<DeleteRoleCommand, Role> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<DeleteRoleCommand> message) throws Exception {
        Role role = this.getRepository().business().get(message.getCommand().getId());
        this.getRepository().add(role.delete());
        return CommandResponse.ok();
    }
}
