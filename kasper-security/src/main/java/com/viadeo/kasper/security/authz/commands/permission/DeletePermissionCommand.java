package com.viadeo.kasper.security.authz.commands.permission;


import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a Permission for security authorization")
public class DeletePermissionCommand extends DeleteCommand {

    public DeletePermissionCommand(KasperID id) {
        super(id);
    }
}
