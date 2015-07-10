// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

/**
 *
 * A default {@link com.viadeo.kasper.api.id.KasperID} Long implementation
 * @see com.viadeo.kasper.api.id.KasperID
 *
 */
@Deprecated
public class LongKasperId extends AbstractKasperID<Long> {
    private static final long serialVersionUID = 2557842274331061279L;

    // ------------------------------------------------------------------------

    LongKasperId() {
        super(0L);
    }

    public LongKasperId(final Long id) {
        super(id);
    }

}
