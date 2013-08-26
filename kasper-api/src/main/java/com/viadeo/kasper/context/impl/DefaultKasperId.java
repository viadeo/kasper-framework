// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.impl.AbstractKasperID;

import java.util.UUID;

/**
 *
 * A default {@link com.viadeo.kasper.KasperID} implementation
 * @see com.viadeo.kasper.KasperID
 *
 */
public class DefaultKasperId extends AbstractKasperID<UUID> {
    private static final long serialVersionUID = 2557821277131061279L;

    DefaultKasperId() {
        super(UUID.randomUUID());
    }

    public DefaultKasperId(final UUID userId) {
        super(userId);
    }

    public DefaultKasperId(final String userId) {
        super(UUID.fromString(userId));
    }

}
