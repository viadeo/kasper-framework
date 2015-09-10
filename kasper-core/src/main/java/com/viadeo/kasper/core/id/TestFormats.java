// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.FormatAdapter;

public final class TestFormats {

    private TestFormats() {}

    public static final Format UUID = new FormatAdapter("uuid", java.util.UUID.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) java.util.UUID.fromString(identifier);
        }
    };

    public static final Format ID = new FormatAdapter("id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };

    public static final Format STRING = new FormatAdapter("string", String.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) String.valueOf(identifier);
        }
    };
}
