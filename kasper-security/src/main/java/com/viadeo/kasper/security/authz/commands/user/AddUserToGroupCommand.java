package com.viadeo.kasper.security.authz.commands.user;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Remove a Permission from a Group for security authorization")
public class AddUserToGroupCommand implements Command {

    @NotNull
    private KasperID userId;

    @NotNull
    private KasperID groupId;

    public AddUserToGroupCommand(final KasperID groupId, final KasperID userId) {
        this.groupId = checkNotNull(groupId);
        this.userId = checkNotNull(userId);
    }

    public KasperID getUserId() {
        return userId;
    }

    public KasperID getGroupId() {
        return groupId;
    }
}
