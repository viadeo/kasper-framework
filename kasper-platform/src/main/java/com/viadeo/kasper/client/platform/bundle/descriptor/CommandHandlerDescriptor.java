// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.bundle.descriptor;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandHandlerDescriptor implements KasperComponentDescriptor {

    private final Class<? extends CommandHandler> commandHandlerClass;
    private final Class<? extends Command> commandClass;

    // ------------------------------------------------------------------------

    public CommandHandlerDescriptor(final Class<? extends CommandHandler> commandHandlerClass,
                                    final Class<? extends Command> commandClass) {
        this.commandHandlerClass = checkNotNull(commandHandlerClass);
        this.commandClass = checkNotNull(commandClass);
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<? extends CommandHandler> getReferenceClass() {
        return commandHandlerClass;
    }

    public Class<? extends Command> getCommandClass() {
        return commandClass;
    }

}
