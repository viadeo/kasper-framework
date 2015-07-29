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
 * @see CommandResponse
 * @see Context
 */
public interface CommandHandler<COMMAND extends Command>
        extends Handler<CommandResponse, COMMAND>
{

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
    @Override
    CommandResponse handle(Context context, COMMAND command) throws Exception;

    @Override
    Class<? extends CommandHandler> getHandlerClass();
}
