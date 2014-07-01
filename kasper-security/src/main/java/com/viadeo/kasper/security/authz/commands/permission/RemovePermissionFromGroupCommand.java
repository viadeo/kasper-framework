package com.viadeo.kasper.security.authz.commands.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Remove a Permission from a Group for security authorization")
public class RemovePermissionFromGroupCommand implements Command {

    @NotNull
    private KasperID permissionId;

    @NotNull
    private KasperID groupId;

    public RemovePermissionFromGroupCommand(final KasperID groupId, final KasperID permissionId) {
        this.groupId = checkNotNull(groupId);
        this.permissionId = checkNotNull(permissionId);
    }

    public KasperID getPermissionId() {
        return permissionId;
    }

    public KasperID getGroupId() {
        return groupId;
    }
}