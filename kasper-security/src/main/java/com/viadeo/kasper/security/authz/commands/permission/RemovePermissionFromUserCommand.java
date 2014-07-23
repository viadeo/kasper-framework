// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.permission;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Remove a Permission from a User for security authorization")
public class RemovePermissionFromUserCommand implements Command {

    @NotNull
    private KasperID permissionId;

    @NotNull
    private KasperID userId;

    public RemovePermissionFromUserCommand(final KasperID userId, final KasperID permissionId) {
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
