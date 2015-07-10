package com.viadeo.kasper.core.ids;

import com.viadeo.kasper.api.FormatAdapter;

import java.util.UUID;

public class UUIDFormat extends FormatAdapter {

    public UUIDFormat() {
        super("uuid", UUID.class);
    }

    @Override
    public <E> E parseIdentifier(String identifier) {
        return (E) java.util.UUID.fromString(identifier);
    }

}
