// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.impl;

/**
 *
 * A default {@link com.viadeo.kasper.KasperID} String implementation
 * @see com.viadeo.kasper.KasperID
 *
 */
public class StringKasperId extends AbstractKasperID<String> {
    private static final long serialVersionUID = 2557821277131061279L;

    StringKasperId() {
        super("0");
    }

    public StringKasperId(final String id) {
        super(id);
    }

}
