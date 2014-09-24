// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.role;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a Role for security authorization")
public class DeleteRoleCommand extends DeleteCommand {

    public DeleteRoleCommand(final KasperID id) {
        super(id);
    }

}
