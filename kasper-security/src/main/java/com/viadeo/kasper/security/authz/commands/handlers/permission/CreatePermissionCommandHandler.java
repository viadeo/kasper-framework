// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.CreatePermissionCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

@XKasperCommandHandler(domain = Authorization.class, description = "Create a permission for authorizations")
public class CreatePermissionCommandHandler extends EntityCommandHandler<CreatePermissionCommand, WildcardPermission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<CreatePermissionCommand> message) throws Exception {
        try {
            final WildcardPermission permission = new WildcardPermission(message.getCommand().getPermission());
            this.getRepository().add(permission);
            return CommandResponse.ok();
        } catch (Exception e) {
            return CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "unable to create permission [" + message.getCommand().getPermission() + "]"));
        }
    }

}
