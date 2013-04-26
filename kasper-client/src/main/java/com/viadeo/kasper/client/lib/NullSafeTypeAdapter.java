/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client.lib;

/**
 * Allows to not worry about null values in TypeAdapters.
 * It is used by wrapping it around a typeadapter. Actually it is done by default
 * for every typeadapter (default and custom ones).
 * @param <T> the type of objects this adapter is dealing with.
 */
public class NullSafeTypeAdapter<T> extends TypeAdapter<T> {
    private final TypeAdapter<T> decoratedAdapter;

    public NullSafeTypeAdapter(TypeAdapter<T> decoratedAdapter) {
        this.decoratedAdapter = decoratedAdapter;
    }

    @Override
    public void adapt(final T value, final QueryBuilder builder) {
        if (null != value) {
            decoratedAdapter.adapt(value, builder);
        }
    }
}
