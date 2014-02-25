// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain;

import com.google.common.base.Objects;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.Event;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base implementation for entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class EntityCreatedEvent<D extends Domain> extends Event implements EntityEvent<D> {

	private static final long serialVersionUID = -1948165707419476512L;

	private final KasperID entityId;

	// ------------------------------------------------------------------------

    protected EntityCreatedEvent(final KasperID entityId) {
        this.entityId = checkNotNull(entityId);
    }

	// ------------------------------------------------------------------------

	public KasperID getEntityId() {
		return this.entityId;
	}

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.entityId);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final EntityCreatedEvent other = (EntityCreatedEvent) obj;
        return Objects.equal(this.entityId, other.entityId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.entityId)
                .toString();
    }

}
