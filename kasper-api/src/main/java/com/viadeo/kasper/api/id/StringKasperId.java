// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

/**
 *
 * A default {@link KasperID} String implementation
 * @see KasperID
 * @deprecated use <code>ID</code> instead
 */
@Deprecated
public class StringKasperId extends AbstractKasperID<String> {
    private static final long serialVersionUID = 2557421277131061279L;

    // ------------------------------------------------------------------------

    StringKasperId() {
        super("0");
    }

    public StringKasperId(final String id) {
        super(id);
    }

}
