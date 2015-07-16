// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.viadeo.kasper.api.id.FormatAdapter;

import java.util.UUID;

public class UUIDFormat extends FormatAdapter {

    public UUIDFormat() {
        super("uuid", UUID.class);
    }

    @Override
    public <E> E parseIdentifier(final String identifier) {
        return (E) java.util.UUID.fromString(identifier);
    }

}
