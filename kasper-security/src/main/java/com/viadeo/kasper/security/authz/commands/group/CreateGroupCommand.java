// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.group;

import com.viadeo.kasper.cqrs.command.CreateCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.impl.DefaultKasperId;

import javax.validation.constraints.NotNull;

@XKasperCommand(description = "Create a Group for security authorization")
public class CreateGroupCommand extends CreateCommand {

    @NotNull
    private final String name;

    public CreateGroupCommand(final String name) {
        super(new DefaultKasperId());
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
