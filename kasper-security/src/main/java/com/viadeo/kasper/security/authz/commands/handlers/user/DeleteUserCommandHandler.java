// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.user;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.DeleteUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;

@XKasperCommandHandler(domain = Authorization.class, description = "Delete a user for authorizations")
public class DeleteUserCommandHandler extends EntityCommandHandler<DeleteUserCommand, User> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<DeleteUserCommand> message) throws Exception {
        final User user = this.getRepository().business().load(message.getCommand().getId());
        user.delete();
        return CommandResponse.ok();
    }

}
