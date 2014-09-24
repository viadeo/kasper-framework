// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.commands.group;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.DeleteCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;

@XKasperCommand(description = "Delete a Group for security authorization")
public class DeleteGroupCommand extends DeleteCommand {

    public DeleteGroupCommand(final KasperID groupId) {
        super(groupId);
    }

}
