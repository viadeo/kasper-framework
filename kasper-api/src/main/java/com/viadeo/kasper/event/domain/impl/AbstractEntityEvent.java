// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import com.google.common.base.Objects;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.EntityEvent;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base implementation for entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class AbstractEntityEvent<D extends Domain>
        extends AbstractDomainEvent<D> implements EntityEvent<D> {

	private static final long serialVersionUID = -1948165707419476512L;

	private final KasperID entityId;
    private final Long version;
	private final DateTime lastEntityModificationDate;

	// ------------------------------------------------------------------------

    protected AbstractEntityEvent(final KasperID id,
                                  final Long version,
                                  final DateTime lastModificationDate) {
        super();

        this.entityId = checkNotNull(id);
        this.version = (null == version) ? 0L : version;
        this.lastEntityModificationDate = checkNotNull(lastModificationDate);
    }

	protected AbstractEntityEvent(final Context context,
                                  final KasperID id,
                                  final Long version,
                                  final DateTime lastModificationDate) {
        super(context);

		this.entityId = checkNotNull(id);
        this.version = (null == version) ? 0L : version;
		this.lastEntityModificationDate = checkNotNull(lastModificationDate);
	}

	// ------------------------------------------------------------------------

	@Override
	public KasperID getEntityId() {
		return this.entityId;
	}

    @Override
	public DateTime getEntityLastModificationDate() {
		return this.lastEntityModificationDate;
	}

    @Override
    public Long getVersion() {
        return this.version;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(
                super.hashCode(), this.entityId,
                this.version, this.lastEntityModificationDate
        );
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == checkNotNull(obj)) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final AbstractEntityEvent other = (AbstractEntityEvent) obj;

        return super.equals(obj) &&
                Objects.equal(this.entityId, other.entityId) &&
                Objects.equal(this.version, other.version);
                /* do not compare the date */
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.entityId)
                .addValue(this.version)
                .addValue(this.lastEntityModificationDate)
                .toString();
    }

}
