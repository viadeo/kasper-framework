// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.handlers.group;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.group.CreateGroupCommand;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import org.axonframework.repository.AggregateNotFoundException;

@XKasperCommandHandler(domain = Authorization.class, description = "Create a group for authorizations")
public class CreateGroupCommandHandler extends EntityCommandHandler<CreateGroupCommand, Group> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<CreateGroupCommand> message) throws Exception {
        try {
            final Group group = new Group(message.getCommand().getIdToUse(), message.getCommand().getName());
            this.getRepository().add(group);
            return CommandResponse.ok();
        } catch (Exception e) {
            return CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "unable to create group [" + message.getCommand().getName() + "]"));
        }
    }

}
