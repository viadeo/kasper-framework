// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import org.axonframework.commandhandling.CommandMessage;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Decorator for Axon command message, holds an ICommand for bus traversal
 *
 * @param <C> Command
 */
public class KasperCommandMessage<C extends Command> implements Serializable {

	private static final long serialVersionUID = 5946300419038957372L;

	/**
	 * Decored Axon command message
	 */
	private final CommandMessage<C> decoredMessage;

	// ------------------------------------------------------------------------

	/**
	 * @param decoredMessage the Axon decored command to wrap
	 */
	public KasperCommandMessage(final CommandMessage<C> decoredMessage) {
		this.decoredMessage = checkNotNull(decoredMessage);
	}

	// ------------------------------------------------------------------------

	/**
	 * Extract context from Axon command message metadata
	 * If no context has been provided during command sending, an empty context then be used
     * @return the context embedded in this message instance
	 */
	public Context getContext() {
		return Objects.firstNonNull(
                (Context) this.decoredMessage.getMetaData().get(Context.METANAME),
                Contexts.empty()
        );
	}
	
	// ------------------------------------------------------------------------

	public C getCommand() {
		return this.decoredMessage.getPayload();
	}

}
