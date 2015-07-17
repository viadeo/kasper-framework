// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import java.util.UUID;

public class UuidFormat extends FormatAdapter {

    public UuidFormat() {
        super("uuid", UUID.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E parseIdentifier(String identifier) {
        return (E) UUID.fromString(identifier);
    }
}
