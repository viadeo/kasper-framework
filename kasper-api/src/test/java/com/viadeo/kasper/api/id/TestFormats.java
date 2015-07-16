package com.viadeo.kasper.api.id;

import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.FormatAdapter;

public final class TestFormats {

    private TestFormats() { }

    public static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };

    public static final Format UUID = new FormatAdapter("uuid", java.util.UUID.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) java.util.UUID.fromString(identifier);
        }
    };
}
