package com.viadeo.kasper.security.authz.commands.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Remove a Permission from a Role for security authorization")
public class RemovePermissionFromRoleCommand implements Command {

    @NotNull
    private KasperID permissionId;

    @NotNull
    private KasperID roleId;

    public RemovePermissionFromRoleCommand(final KasperID roleId, final KasperID permissionId) {
        this.roleId = checkNotNull(roleId);
        this.permissionId = checkNotNull(permissionId);
    }

    public KasperID getPermissionId() {
        return permissionId;
    }

    public KasperID getRoleId() {
        return roleId;
    }
}

