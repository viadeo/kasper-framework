// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.Handler;

/**
 * A <code>CommandHandler</code> is invoked to process a <code>Command</code> request.
 *
 * @param <COMMAND> the command class handled by this <code>CommandHandler</code>.
 *
 * @see Command
 * @see CommandMessage
 * @see CommandResponse
 * @see Context
 */
public interface CommandHandler<COMMAND extends Command>
        extends Handler<CommandMessage<COMMAND>, CommandResponse, COMMAND>
{

    /**
     * Generic parameter position for the handled command
     */
    int COMMAND_PARAMETER_POSITION = 0;

    /**
     * Handle the <code>CommandMessage</code>.
     *
     * @param message the command message
     * @return a response
     */
    @Override
    CommandResponse handle(CommandMessage<COMMAND> message);

    @Override
    Class<? extends CommandHandler> getHandlerClass();
}
