// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Add a Role to a User for security authorization")
public class AddRoleToUserCommand implements Command {

    @NotNull
    private KasperID roleId;

    @NotNull
    private KasperID userId;

    public AddRoleToUserCommand(final KasperID userId, final KasperID roleId) {
        this.userId = checkNotNull(userId);
        this.roleId = checkNotNull(roleId);
    }

    public KasperID getRoleId() {
        return roleId;
    }

    public KasperID getUserId() {
        return userId;
    }

}
