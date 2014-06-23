package com.viadeo.kasper.security.authz.commands.role;


import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a Role for security authorization")
public class DeleteRoleCommand extends DeleteCommand {

    public DeleteRoleCommand(KasperID id) {
        super(id);
    }
}
