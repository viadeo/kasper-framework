// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

import javax.validation.constraints.NotNull;

@XKasperCommand(description = "Create a Role for security authorization")
public class CreateRoleCommand extends CreateCommand {

    @NotNull
    private final String name;

    public CreateRoleCommand(final KasperID kasperID, final String name) {
        super(kasperID);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
