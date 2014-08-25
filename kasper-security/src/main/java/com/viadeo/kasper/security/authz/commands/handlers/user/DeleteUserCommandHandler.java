// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.user;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.DeleteUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Delete a user for authorizations")
public class DeleteUserCommandHandler extends EntityCommandHandler<DeleteUserCommand, User> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<DeleteUserCommand> message) throws Exception {
        try {
            final User user = this.getRepository().business().get(message.getCommand().getId());
            user.delete();
            return CommandResponse.ok();
        } catch (AggregateNotFoundException e) {
            return CommandResponse.error(new KasperReason(CoreReasonCode.INVALID_INPUT, "unable to find user to delete"));
        }
    }

}
