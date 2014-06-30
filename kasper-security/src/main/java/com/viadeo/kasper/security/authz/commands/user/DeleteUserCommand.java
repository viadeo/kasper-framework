package com.viadeo.kasper.security.authz.commands.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a User for security authorization")
public class DeleteUserCommand extends DeleteCommand {

    public DeleteUserCommand(KasperID id) {
        super(id);
    }
}
