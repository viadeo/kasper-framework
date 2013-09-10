// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;

public abstract class AbstractEvent implements Event {
	private static final long serialVersionUID = 7345041953962326298L;

	private transient Context context;

	// ------------------------------------------------------------------------

    protected AbstractEvent() { /* for serialization */ }

	protected AbstractEvent(final Context context) {
        this.context = Preconditions.checkNotNull(context);
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.Event#getContext()
	 */
	@Override
	public Optional<Context> getContext() {
		return Optional.fromNullable(this.context);
	}

	/**
	 * @see com.viadeo.kasper.event.Event#setContext(com.viadeo.kasper.context.Context)
	 */
	@Override
	public <E extends Event> E setContext(final Context context) {
		this.context = context;
        return (E) this;
	}

}
