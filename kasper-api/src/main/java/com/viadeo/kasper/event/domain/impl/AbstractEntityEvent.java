// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

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

}
