// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.role;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.role.CreateRoleCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

@XKasperCommandHandler(domain = Authorization.class, description = "Create a role for authorizations")
public class CreateRoleCommandHandler extends EntityCommandHandler<CreateRoleCommand, Role> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<CreateRoleCommand> message) throws Exception {
        try {
            final Role role = new Role(message.getCommand().getName());
            this.getRepository().add(role);
            return CommandResponse.ok();
        } catch (Exception e) {
            return CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "unable to create role [" + message.getCommand().getName() + "]"));
        }
    }

}
