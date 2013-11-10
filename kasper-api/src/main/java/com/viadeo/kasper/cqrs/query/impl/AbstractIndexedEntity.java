// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.IndexedEntity;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * BAse implementation for an indexed entity
 */
public abstract class AbstractIndexedEntity implements IndexedEntity {

    private final KasperID id;
    private final String type;

    private DateTime lastModificationDate;
    private Long version ;

    // ------------------------------------------------------------------------

    protected AbstractIndexedEntity(final KasperID id, final String type) {
        this.id = checkNotNull(id);
        this.type = checkNotNull(type);
        this.version = null;
        this.lastModificationDate = new DateTime(0L);
    }

    protected AbstractIndexedEntity(final KasperID id, final String type,
                                    final Long version, final DateTime lastModificationDate) {
        this.id = checkNotNull(id);
        this.type = checkNotNull(type);
        this.version = checkNotNull(version);
        this.lastModificationDate = checkNotNull(lastModificationDate);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperID getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Optional<Long> getVersion() {
        return Optional.fromNullable(this.version);
    }

    @Override
    @SuppressWarnings("unchecked") // Must be checked by client
    public <I extends IndexedEntity> I setVersion(final Long version) {
        this.version = checkNotNull(version);
        return (I) this;
    }

    @Override
    public DateTime getLastModificationDate() {
        return this.lastModificationDate;
    }

    @Override
    @SuppressWarnings("unchecked") // Must be checked by client
    public <I extends IndexedEntity> I setLastModificationDate(final DateTime date) {
       this.lastModificationDate = checkNotNull(date);
       return (I) this;
    }

}
