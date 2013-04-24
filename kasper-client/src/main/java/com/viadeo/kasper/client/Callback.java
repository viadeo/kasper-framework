/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

public interface Callback<T> {
    public void done(T object);
}
