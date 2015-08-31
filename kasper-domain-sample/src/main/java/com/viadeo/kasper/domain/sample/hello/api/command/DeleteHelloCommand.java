// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.command;

import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.component.command.DeleteCommand;
import com.viadeo.kasper.api.id.KasperID;

/**
 * It's a command designed to delete an entity, so let's mark it with DeleteCommand
 */
@XKasperCommand(description = "Delete an existing hello message")
public class DeleteHelloCommand extends DeleteCommand {

    public DeleteHelloCommand(final KasperID id) {
        super(id);
    }

}
