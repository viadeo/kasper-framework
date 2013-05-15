
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.CurrentContext;
import com.viadeo.kasper.event.IEvent;

public abstract class AbstractEvent implements IEvent {

	private static final long serialVersionUID = 7345041953962326298L;

	private transient IContext context;
	
	// ------------------------------------------------------------------------
	
	protected AbstractEvent() {
		if (CurrentContext.value().isPresent()) {
			this.context = CurrentContext.value().get();
		}
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @see com.viadeo.kasper.event.IEvent#getContext()
	 */
	@Override
	public Optional<IContext> getContext() {
		return Optional.fromNullable(this.context);
	}
	
	/**
	 * @see com.viadeo.kasper.event.IEvent#setContext(com.viadeo.kasper.context.IContext)
	 */
	@Override
	public void setContext(final IContext context) {
		this.context = context;
	}
	
}
