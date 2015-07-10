// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * A default {@link com.viadeo.kasper.api.id.KasperID} implementation
 * @see com.viadeo.kasper.api.id.KasperID
 *
 */
public class DefaultKasperId extends AbstractKasperID<UUID> {
    private static final long serialVersionUID = 2557821277421061279L;

    public static final int UUID_LENGTH = 36;
    public static final String UUID_REGEXP = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    // ------------------------------------------------------------------------

    public static DefaultKasperId random() {
        return new DefaultKasperId();
    }

    // ------------------------------------------------------------------------

    public DefaultKasperId() {
        super(UUID.randomUUID());
    }

    public DefaultKasperId(final UUID id) {
        super(id);
    }

    public DefaultKasperId(final String id) {
        super(UUID.fromString(checkNotNull(id)));
    }

}
