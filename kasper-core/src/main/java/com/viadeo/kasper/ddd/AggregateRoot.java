// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.api.domain.exception.KasperCommandException;
import com.viadeo.kasper.api.domain.event.Event;
import org.axonframework.domain.MetaData;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base AGR implementation
 *
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see com.viadeo.kasper.api.domain.Domain
 */
public abstract class AggregateRoot<I extends KasperID>
		extends AbstractAnnotatedAggregateRoot<KasperID>
        implements Entity {

	private static final long serialVersionUID = 8352516744342839116L;

    public static final String VERSION_METANAME = "VERSION";

	@AggregateIdentifier
	protected I id;

    private Long version;

	// ========================================================================

	@SuppressWarnings("unchecked")
	public I  getEntityId() {
		return this.id;
	}

    protected void setId(final I id) {
        this.id = checkNotNull(id);
    }

    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------

    @Override
    protected void apply(final Object eventPayload) {
        apply(checkNotNull(eventPayload), enrichMetaData(null));
    }

    @Override
    protected void apply(final Object eventPayload, final MetaData metaData) {
        checkNotNull(eventPayload);
        checkNotNull(metaData);

        if ( ! Event.class.isAssignableFrom(eventPayload.getClass())) {
            throw new KasperCommandException("Only apply implementations of 'IEvent'");
        }

        super.apply(eventPayload, enrichMetaData(metaData));
    }

    // ------------------------------------------------------------------------

    protected MetaData enrichMetaData(final MetaData metaData) {
        final Map<String, Object> newMetaData = Maps.newHashMap();

        // Add context
        if (CurrentContext.value().isPresent()) {
            newMetaData.put(Context.METANAME, CurrentContext.value().get());
        }

        // Add version
        final Long version = (null == this.getVersion()) ? 0L : this.getVersion();
        newMetaData.put(AggregateRoot.VERSION_METANAME, version);

        if (null == metaData) {
            return MetaData.from(newMetaData);
        } else {
            return metaData.mergedWith(newMetaData);
        }

    }

}
