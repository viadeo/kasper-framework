package com.viadeo.kasper.security.authz.commands.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Add a Permission to a User for security authorization")
public class AddPermissionToUserCommand implements Command {

    @NotNull
    private KasperID permissionId;

    @NotNull
    private KasperID userId;

    public AddPermissionToUserCommand(final KasperID userId, final KasperID permissionId) {
        this.userId = checkNotNull(userId);
        this.permissionId = checkNotNull(permissionId);
    }

    public KasperID getPermissionId() {
        return permissionId;
    }

    public KasperID getUserId() {
        return userId;
    }
}
