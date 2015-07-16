package com.viadeo.kasper.api.id;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract adapter class for defining a format.
 */
public abstract class FormatAdapter implements Format {

    private final String name;
    private final Class<?> identifierType;

    protected FormatAdapter(final String name, final Class<?> identifierType) {
        this.name = checkNotNull(name);
        this.identifierType = checkNotNull(identifierType);
    }

    @Override
    public boolean accept(final Object identifier) {
        return identifierType().isInstance(identifier);
    }

    @Override
    public Class<?> identifierType() {
        return identifierType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name();
    }
}
