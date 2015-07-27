// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import org.axonframework.commandhandling.CommandMessage;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Decorator for Axon command message, holds an ICommand for bus traversal
 *
 * @param <C> Command
 */
public class KasperCommandMessage<C extends Command> implements Serializable {

	private static final long serialVersionUID = 5946300419038957372L;

    private final Context context;
    private final C command;

    // ------------------------------------------------------------------------

	/**
     * Extract context from Axon command message metadata
     * If no context has been provided during command sending, an empty context then be used
	 * @param decoredMessage the Axon decored command to wrap
	 */
	public KasperCommandMessage(final CommandMessage<C> decoredMessage) {
        this(
                Objects.firstNonNull(
                        (Context) decoredMessage.getMetaData().get(Context.METANAME),
                        Contexts.empty()
                ),
                decoredMessage.getPayload()
        );
	}

    /**
     * @param context a context
     * @param command a command
     */
    public KasperCommandMessage(final Context context, final C command) {
        this.context = checkNotNull(context);
        this.command = checkNotNull(command);
    }

	// ------------------------------------------------------------------------

	public Context getContext() {
		return context;
	}
	
	// ------------------------------------------------------------------------

	public C getCommand() {
		return command;
	}

}
