package com.viadeo.kasper.security.authz.commands.group;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a Group for security authorization")
public class DeleteGroupCommand extends DeleteCommand {

    public DeleteGroupCommand(KasperID groupId) {
        super(groupId);
    }
}
