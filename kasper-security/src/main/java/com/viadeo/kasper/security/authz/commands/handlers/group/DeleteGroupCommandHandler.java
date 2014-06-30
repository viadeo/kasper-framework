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
        Group group = this.getRepository().business().get(message.getCommand().getId());
        this.getRepository().add(group.delete());
        return CommandResponse.ok();
    }
}

