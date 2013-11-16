// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base AGR implementation
 *
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see com.viadeo.kasper.ddd.Domain
 */
public abstract class AbstractAggregateRoot<I extends KasperID>
		extends AbstractAnnotatedAggregateRoot<KasperID>
		implements AggregateRoot<I> {
	
	private static final long serialVersionUID = 8352516744342839116L;

    private Long version;

	@AggregateIdentifier
	protected I id;
	
	private DateTime creationDate;
	
	private DateTime modificationDate;	
	
	// ========================================================================

	@SuppressWarnings("unchecked")
	@Override
	public I  getEntityId() {
		return this.id;
	}

    protected void setId(final I id) {
        this.id = id;
    }

    // ========================================================================

    @Override
    public void setVersion(final Long version) {
        if (null == super.getVersion()) { /* if aggregate is not event-sourced */
            this.version = checkNotNull(version);
        }
    }

    /**
     * A newly created aggregate will have version null
     * A firstly loaded aggregate (new aggregate, first loaded) will have version 0L
     */
    @Override
    public Long getVersion() {
        final Long superVersion = super.getVersion();
        if (null == superVersion) { /* if aggregate is not event-sourced */
            return this.version;
        }
        return superVersion;
    }

	// ========================================================================
	
	public DateTime getCreationDate() {
		return this.creationDate;
	}
	
	public DateTime getModificationDate() {
		return this.modificationDate;
	}

	protected void setCreationDate(final DateTime creationDate) {
		this.creationDate = checkNotNull(creationDate);
		this.modificationDate = creationDate;
	}
	
	protected void setModificationDate(final DateTime modificationDate) {
		this.modificationDate = checkNotNull(modificationDate);
	}
	
}
