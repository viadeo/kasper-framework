/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

public interface QueryFactory {
    public <T> TypeAdapter<T> create(TypeToken<T> typeToken);
}
