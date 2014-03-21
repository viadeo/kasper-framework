// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * The Kasper event
 *
 */
public abstract class Event implements IEvent {

    /**
     * Optional Unit Of Work macro event parent id
     * ( set during uow commit() )
     */
    private String uowEventId;

    /**
     * Event type, can be set by the repository before persisting
     */
    private PersistencyType type = PersistencyType.UNKNOWN;

    // ------------------------------------------------------------------------

    public enum PersistencyType {
        UNKNOWN,       /* not yet assigned */
        EVENT_SOURCE,  /* event is used by event sourcing strategy repository */
        EVENT_INFO     /* event is used by entity store strategy repository */
    }

    // ------------------------------------------------------------------------

    public void setPersistencyType(final PersistencyType type) {
        this.type = checkNotNull(type);
    }

    public PersistencyType getPersistencyType() {
        return this.type;
    }

    // ------------------------------------------------------------------------

    public Optional<String> getUOWEventId() {
        return Optional.fromNullable(this.uowEventId);
    }

    public void setUOWEventId(final String uowEventId) {
        this.uowEventId = uowEventId;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.type)
                .toString();
    }

}
