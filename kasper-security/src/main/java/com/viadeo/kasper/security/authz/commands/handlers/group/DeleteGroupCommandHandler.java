// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.group;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.group.DeleteGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;

@XKasperCommandHandler(domain = Authorization.class, description = "Delete a group for authorizations")
public class DeleteGroupCommandHandler extends EntityCommandHandler<DeleteGroupCommand, Group> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<DeleteGroupCommand> message) throws Exception {
        final Group group = this.getRepository().business().get(message.getCommand().getId());
        group.delete();
        return CommandResponse.ok();
    }

}

