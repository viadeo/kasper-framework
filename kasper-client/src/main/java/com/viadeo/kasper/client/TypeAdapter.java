/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

public abstract class TypeAdapter<T> {
    public abstract void adapt(T value, QueryBuilder builder);

    public final TypeAdapter<T> skipNull() {
        return new TypeAdapter<T>() {
            @Override
            public void adapt(T value, QueryBuilder builder) {
                if (value != null) {
                    TypeAdapter.this.adapt(value, builder);
                }
            }
        };
    }
}
