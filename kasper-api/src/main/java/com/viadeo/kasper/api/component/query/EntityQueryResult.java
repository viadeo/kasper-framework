// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.query;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.id.KasperID;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class EntityQueryResult implements QueryResult {

    private final KasperID id;
    private final String type;
    private final Long version;
    private final DateTime lastModificationDate;

    // ------------------------------------------------------------------------

    public EntityQueryResult(final KasperID id, final String type, final Long version) {
        this(id, type, version, new DateTime(0L));
    }

    public EntityQueryResult(final KasperID id, final String type, final Long version, final DateTime time) {
        this.id = checkNotNull(id);
        this.type = checkNotNull(type);
        this.version = checkNotNull(version);
        this.lastModificationDate = checkNotNull(time);
    }

    // ------------------------------------------------------------------------

    public KasperID getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public Long getVersion() {
        return this.version;
    }

    public DateTime getLastModificationDate() {
        return this.lastModificationDate;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.type, this.version, this.lastModificationDate);
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == checkNotNull(obj)) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final EntityQueryResult other = (EntityQueryResult) obj;

        return Objects.equal(this.id, other.id) &&
                Objects.equal(this.type, other.type) &&
                Objects.equal(this.version, other.version) &&
                Objects.equal(this.lastModificationDate, other.lastModificationDate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.type)
                .addValue(this.version)
                .addValue(this.lastModificationDate)
                .toString();
    }

}
