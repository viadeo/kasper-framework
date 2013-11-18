// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.event.Event.PersistencyType.UNKNOWN;

public abstract class AbstractEvent implements Event {
	private static final long serialVersionUID = 7345041953962326298L;

	private Context context;
    private PersistencyType persistencyType = UNKNOWN;

	// ------------------------------------------------------------------------

    protected AbstractEvent() { }

	protected AbstractEvent(final Context context) {
        this.context = checkNotNull(context);
	}

    // -----------------------------------------------------------------------

    @Override
    public PersistencyType getPersistencyType() {
        return this.persistencyType;
    }

    @Override
    public void setPersistencyType(final PersistencyType persistencyType) {
        this.persistencyType = checkNotNull(persistencyType);
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
    @SuppressWarnings("unchecked") // Client must ensure correct type
	public <E extends Event> E setContext(final Context context) {
		this.context = context;
        return (E) this;
	}

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.context, this.persistencyType);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final AbstractEvent other = (AbstractEvent) obj;

        return Objects.equal(this.context, other.context);
               /* do not compare persistency type */
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.context)
                .addValue(this.persistencyType)
                .toString();
    }

}
