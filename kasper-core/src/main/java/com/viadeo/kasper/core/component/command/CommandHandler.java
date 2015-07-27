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

/**
 * A <code>CommandHandler</code> is invoked to process a <code>Command</code> request.
 *
 * @param <COMMAND> the command class handled by this <code>CommandHandler</code>.
 *
 * @see Command
 * @see CommandResponse
 * @see Context
 */
public interface CommandHandler<COMMAND extends Command> {

    /**
     * Generic parameter position for the handled command
     */
    public static final int COMMAND_PARAMETER_POSITION = 0;

    /**
     * Handle the <code>Command</code> with his <code>Context</code>.
     *
     * @param context the context related to the request
     * @param command the command requested
     * @return a response
     * @throws Exception if an unexpected error happens
     */
    CommandResponse handle(Context context, COMMAND command) throws Exception;

    /**
     * @return the command class handled by this <code>CommandHandler</code>.
     */
    public Class<COMMAND> getCommandClass();

    /**
     * @return the command handler class
     */
    public Class<? extends CommandHandler> getCommandHandlerClass();
}
