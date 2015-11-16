// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

public class UUIDFormat extends FormatAdapter {

    public UUIDFormat() {
        super("uuid", java.util.UUID.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E parseIdentifier(String identifier) {
        return (E) java.util.UUID.fromString(identifier);
    }
}
