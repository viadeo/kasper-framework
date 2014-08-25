// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.permission;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.permission.DeletePermissionCommand;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Delete a permission for authorizations")
public class DeletePermissionCommandHandler extends EntityCommandHandler<DeletePermissionCommand, WildcardPermission> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<DeletePermissionCommand> message) throws Exception {
        try{
            final WildcardPermission permission =
                    this.getRepository().business().get(message.getCommand().getId());
                permission.delete();
                return CommandResponse.ok();
        } catch(AggregateNotFoundException e){
            return CommandResponse.error(new KasperReason(CoreReasonCode.INVALID_INPUT, "unable to find permission to delete"));
        }
    }
}
