package com.viadeo.kasper.security.authz.commands.handlers.user;

import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.commands.user.CreateUserCommand;
import com.viadeo.kasper.security.authz.entities.actor.User;

@XKasperCommandHandler(domain = Authorization.class, description = "Create a User for authorizations")
public class CreateUserCommandHandler extends EntityCommandHandler<CreateUserCommand, User> {

    @Override
    public CommandResponse handle(final KasperCommandMessage<CreateUserCommand> message) throws Exception {
        User user = new User(message.getCommand().getIdToUse(), message.getCommand().getFirstName(), message.getCommand().getLastName());
        this.getRepository().add(user);
        return CommandResponse.ok();
    }
}
