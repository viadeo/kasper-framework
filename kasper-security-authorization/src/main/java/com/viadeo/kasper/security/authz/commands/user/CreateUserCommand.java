package com.viadeo.kasper.security.authz.commands.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Create a User for security authorization")
public class CreateUserCommand  extends CreateCommand {

    public CreateUserCommand(KasperID providedId) {
        super(providedId);
    }
}
