// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.event;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.id.KasperID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base implementation for entity events
 *
 * @see EntityEvent
 */
public abstract class EntityCreatedEvent<D extends Domain> implements EntityEvent<D> {

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
        if (null == obj) {
            return false;
        }

        if (this == checkNotNull(obj)) {
            return true;
        }
        if ( ! getClass().equals(obj.getClass())) {
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
