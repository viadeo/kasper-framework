/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

public interface TypeAdapterFactory {
    public <T> TypeAdapter<T> create(TypeToken<T> typeToken, QueryFactory adapterFactory);
}
