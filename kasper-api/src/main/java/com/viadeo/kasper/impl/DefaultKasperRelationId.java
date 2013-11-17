// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.exception.KasperException;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * A default {@link com.viadeo.kasper.KasperID} implementation
 * @see com.viadeo.kasper.KasperID
 *
 */
public class DefaultKasperRelationId extends AbstractKasperID<String> implements KasperRelationID {
    private static final long serialVersionUID = 2557821277131061279L;

    public static final String SEPARATOR = "--";

    protected KasperID sourceId;
    protected KasperID targetId;

    // ------------------------------------------------------------------------

    public static DefaultKasperRelationId random() {
        return new DefaultKasperRelationId();
    }

    public DefaultKasperRelationId() {
        this.sourceId = new DefaultKasperId(UUID.randomUUID());
        this.targetId = new DefaultKasperId(UUID.randomUUID());
        super.setId(relationIdsToString(this.sourceId, this.targetId));
    }

    public DefaultKasperRelationId(final KasperID sourceId, final KasperID targetId) {
        this.sourceId = checkNotNull(sourceId);
        this.targetId = checkNotNull(targetId);
        super.setId(relationIdsToString(this.sourceId, this.targetId));
    }

    public DefaultKasperRelationId(final String id) {
        final KasperID[] ids = stringToKasperIDs(id);

        this.sourceId = ids[0];
        this.targetId = ids[1];

        super.setId(id);
    }

    public void setId(final String id) {
        final KasperID[] ids = stringToKasperIDs(id);

        this.sourceId = ids[0];
        this.targetId = ids[1];

        super.setId(id);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperID getSourceId() {
        return this.sourceId;
    }

    @Override
    public KasperID getTargetId() {
        return this.targetId;
    }


    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return relationIdsToString(this.sourceId, this.targetId);
    }

    // ------------------------------------------------------------------------

    protected static KasperID[] stringToKasperIDs(final String id) {
        final String[] parts = id.split(SEPARATOR);

        if (parts.length != 2) {
            throw new KasperException("Unable to determine the two parts of a Kasper relation id from : " + id);
        }

        KasperID[] ids = new KasperID[2];
        ids[0] = stringToKasperId(parts[0]);
        ids[1] = stringToKasperId(parts[1]);

        return ids;
    }

    protected static String relationIdsToString(final KasperID sourceId, final KasperID targetId) {
        return String.format("%s%s%s", sourceId.toString(), SEPARATOR, targetId.toString());
    }

    protected static KasperID stringToKasperId(final String id) {
        try {
            final UUID uuid = UUID.fromString(id);
            return new DefaultKasperId(uuid);

        } catch (final IllegalArgumentException e) {
            return new StringKasperId(id);
        }
    }

}


