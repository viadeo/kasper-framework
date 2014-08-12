// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.permission;

import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.impl.DefaultKasperId;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperCommand(description = "Create a Permission for security authorization")
public class CreatePermissionCommand extends CreateCommand {

    @NotNull
    private final String permission;

    public CreatePermissionCommand(final String permission) {
        super(new DefaultKasperId());
        this.permission = checkNotNull(permission);
    }

    public String getPermission() {
        return permission;
    }

}
