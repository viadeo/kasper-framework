// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

/**
 *
 * A default {@link KasperID} Integer implementation
 * @see KasperID
 * @deprecated use <code>ID</code> instead
 */
@Deprecated
public class IntegerKasperId extends AbstractKasperID<Integer> {
    private static final long serialVersionUID = 2557821274331061279L;

    // ------------------------------------------------------------------------

    IntegerKasperId() {
        super(0);
    }

    public IntegerKasperId(final Integer id) {
        super(id);
    }

}
