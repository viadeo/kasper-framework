// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import org.axonframework.commandhandling.CommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.IDefaultContextBuilder;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandMessage;

/**
 *
 * Decorator for Axon command message, holds an ICommand for bus traversal
 *
 * @param <C> Command
 */
public class KasperCommandMessage<C extends ICommand> implements ICommandMessage<C> {
	private final static Logger LOGGER = LoggerFactory.getLogger(KasperCommandMessage.class);

	private static final long serialVersionUID = 5946300419038957372L;

	/**
	 * Decored Axon command message
	 */
	final private CommandMessage<C> decoredMessage;

	/**
	 * (Optional) default context builder, only used if required (no context available)
	 * If absent a default implementation will be used
	 */
	@Autowired
	private IDefaultContextBuilder defaultContextBuilder;

	// ------------------------------------------------------------------------

	/**
	 * @param decoredMessage the Axon decored command to wrap
	 */
	public KasperCommandMessage(final CommandMessage<C> decoredMessage) {
		this.decoredMessage = decoredMessage;
	}

	// ------------------------------------------------------------------------

	/**
	 * Extract context from Axon command message metadata
	 * If no context has been provided during command sending, the provided
	 * default context builder will then be used, failing back to a default
	 * implementation
	 * 
	 * @see IDefaultContextBuilder
	 * @see DefaultContextBuilder
	 * @see com.viadeo.kasper.cqrs.command.ICommandMessage#getContext()
	 */
	@Override
	public IContext getContext() {
		IContext context = (IContext) this.decoredMessage.getMetaData().get(IContext.METANAME);

		if (null == context) {
			if (null != this.defaultContextBuilder) {
				context = this.defaultContextBuilder.buildDefault();
			} else {
				KasperCommandMessage.LOGGER.warn("Defauting to base Kasper default context, no context has been provided and no Spring contextBuilder can be found ");
				context = (new DefaultContextBuilder()).buildDefault();
			}
		}

		return context;
	}
	
	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.cqrs.command.ICommandMessage#getCommand()
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
	public void setDefaultContextBuilder(final IDefaultContextBuilder defaultContextBuilder) {
		this.defaultContextBuilder = defaultContextBuilder;
	}

}
