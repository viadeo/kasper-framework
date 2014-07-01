package com.viadeo.kasper.security.authz.commands.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Add a Role to a Group for security authorization")
public class AddRoleToGroupCommand implements Command{

    @NotNull
    private KasperID roleId;

    @NotNull
    private KasperID groupId;

    public AddRoleToGroupCommand(final KasperID groupId, final KasperID roleId) {
        this.groupId = checkNotNull(groupId);
        this.roleId = checkNotNull(roleId);
    }

    public KasperID getRoleId() {
        return roleId;
    }

    public KasperID getGroupId() {
        return groupId;
    }
}