// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.ContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.KasperCommandMessage;
import org.axonframework.commandhandling.CommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Decorator for Axon command message, holds an ICommand for bus traversal
 *
 * @param <C> Command
 */
public class DefaultKasperCommandMessage<C extends Command> implements KasperCommandMessage<C> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKasperCommandMessage.class);

	private static final long serialVersionUID = 5946300419038957372L;

	/**
	 * Decored Axon command message
	 */
	private final transient CommandMessage<C> decoredMessage;

	/**
	 * (Optional) default context builder, only used if required (no context available)
	 * If absent a default implementation will be used
	 */
	@Autowired
	private transient ContextBuilder defaultContextBuilder;

	// ------------------------------------------------------------------------

	/**
	 * @param decoredMessage the Axon decored command to wrap
	 */
	public DefaultKasperCommandMessage(final CommandMessage<C> decoredMessage) {
		this.decoredMessage = decoredMessage;
	}

	// ------------------------------------------------------------------------

	/**
	 * Extract context from Axon command message metadata
	 * If no context has been provided during command sending, the provided
	 * default context builder will then be used, failing back to a default
	 * implementation
	 * 
	 * @see com.viadeo.kasper.context.ContextBuilder
	 * @see com.viadeo.kasper.context.impl.DefaultContextBuilder
	 * @see com.viadeo.kasper.cqrs.command.KasperCommandMessage#getContext()
	 */
	@Override
	public Context getContext() {
		Context context = (Context) this.decoredMessage.getMetaData().get(Context.METANAME);

		if (null == context) {
			if (null != this.defaultContextBuilder) {
				context = this.defaultContextBuilder.build();
			} else {
				DefaultKasperCommandMessage.LOGGER.warn("Defauting to base Kasper default context, no context has been provided and no Spring contextBuilder can be found ");
				context = new com.viadeo.kasper.context.impl.DefaultContextBuilder().build();
			}
		}

		return context;
	}
	
	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.command.KasperCommandMessage#getCommand()
	 */
	@Override
	public C getCommand() {
		return this.decoredMessage.getPayload();
	}

	// ------------------------------------------------------------------------

	/**
	 * @param defaultContextBuilder the default context builder to be used if no
	 *         context has been provided during command processing on bus
	 */
	public void setDefaultContextBuilder(final ContextBuilder defaultContextBuilder) {
		this.defaultContextBuilder = defaultContextBuilder;
	}

}
