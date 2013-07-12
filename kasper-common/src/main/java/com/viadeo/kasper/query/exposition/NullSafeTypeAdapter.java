/*
 * Copyright 2013 Viadeo.com
 */
package com.viadeo.kasper.query.exposition;

/**
 * Allows to not worry about null values in TypeAdapters. It is used by wrapping
 * it around a typeadapter. Actually it is done by default for every typeadapter
 * (default and custom ones).
 * 
 * @param <T>
 *            the type of objects this adapter is dealing with.
 */
public class NullSafeTypeAdapter<T> implements TypeAdapter<T> {
	private final TypeAdapter<T> decoratedAdapter;

    // ------------------------------------------------------------------------

	public NullSafeTypeAdapter(final TypeAdapter<T> decoratedAdapter) {
		this.decoratedAdapter = decoratedAdapter;
	}

	public static <T> NullSafeTypeAdapter<T> nullSafe(final TypeAdapter<T> adapter) {
		return new NullSafeTypeAdapter<T>(adapter);
	}

    // ------------------------------------------------------------------------

	@Override
	public void adapt(final T value, final QueryBuilder builder) throws Exception {
		if (null != value) {
			decoratedAdapter.adapt(value, builder);
		} else {
			builder.singleNull();
        }
	}

	@Override
	public T adapt(final QueryParser parser) throws Exception {
		/*
		 * FIXME I am not sure it is ok, null safe should also ensure people
		 * dont have to deal with pairs that have a key but no value, actually
		 * their adapt method would still be called...
		 */
		return decoratedAdapter.adapt(parser);
	}

    // ------------------------------------------------------------------------

	public TypeAdapter<T> unwrap() {
		return decoratedAdapter;
	}

}
