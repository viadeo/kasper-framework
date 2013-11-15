// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.impl.DefaultKasperId;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractEvent implements Event {
	private static final long serialVersionUID = 7345041953962326298L;

    private KasperID uowEventId;
    private KasperID id;
	private Context context;

	// ------------------------------------------------------------------------

    protected AbstractEvent() {
        this.id = new DefaultKasperId();
    }

	protected AbstractEvent(final Context context) {
        this();
        this.context = checkNotNull(context);
	}

	// ------------------------------------------------------------------------

    public Optional<KasperID> getUOWEventId() {
        return Optional.fromNullable(this.uowEventId);
    }

    public void setUOWEventId(final KasperID uowEventId) {
        this.uowEventId = checkNotNull(uowEventId);
    }

    @Override
    public KasperID getId(){
        return this.id;
    }

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
    @SuppressWarnings("unchecked") // Client must ensure correct type
	public <E extends Event> E setContext(final Context context) {
		this.context = context;
        return (E) this;
	}

}
