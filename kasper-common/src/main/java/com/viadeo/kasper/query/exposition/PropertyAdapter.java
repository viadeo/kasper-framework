// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.query.IQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class PropertyAdapter {

	private final Map<Class<?>, Object> DEFAULT_VALUES_FOR_PRIMITIVES = Maps.newHashMap();
	{
		DEFAULT_VALUES_FOR_PRIMITIVES.put(int.class, 0);
		DEFAULT_VALUES_FOR_PRIMITIVES.put(double.class, 0d);
		DEFAULT_VALUES_FOR_PRIMITIVES.put(long.class, 0l);
		DEFAULT_VALUES_FOR_PRIMITIVES.put(short.class, 0);
		DEFAULT_VALUES_FOR_PRIMITIVES.put(float.class, 0f);
		DEFAULT_VALUES_FOR_PRIMITIVES.put(boolean.class, false);
	}

	private final Method mutator;
	private final Method accessor;
	private final String name;
	private final TypeToken<?> typeToken;
	private final ITypeAdapter<Object> adapter;

	// ------------------------------------------------------------------------

	public PropertyAdapter(final TypeToken<?> typeToken, final Method accessor,
			               final Method mutator, final String name,
			               final ITypeAdapter<Object> adapter) {
		this.typeToken = checkNotNull(typeToken);
		this.accessor = checkNotNull(accessor);

		// allow null as it could be a ctr param
		this.mutator = mutator;
		this.name = checkNotNull(name);
		this.adapter = checkNotNull(adapter);

		if (mutator != null) {
			this.mutator.setAccessible(true);
        }
		this.accessor.setAccessible(true);
	}

	// ------------------------------------------------------------------------

	public void adapt(final Object bean, final QueryBuilder builder) {
		try {

			final Object value = accessor.invoke(bean);
			builder.begin(name);
			adapter.adapt(value, builder);
			builder.end();

		} catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw cannotGetPropertyValue(e);
		}
    }

	public Object adapt(final QueryParser parser) {
		final Class<?> rawClass = typeToken.getRawType();

		/*
		 * ok it is ugly but for the moment we have to do that in order to
		 * support composed queries (in fact the problem is more general: we try
		 * to support non flat objects, meaning that a type can be adapted into
		 * multiple key/value pairs but the representation language (url query)
		 * does not have such thing).
		 */

		if (IQuery.class.isAssignableFrom(rawClass)) {

			return adapter.adapt(parser);

		} else if (parser.exists(name)) {

			// in fact in most cases we will do that
			parser.begin(name);
			final Object value = adapter.adapt(parser);
			parser.end();
			return value;

		} else if (rawClass.isPrimitive()) {

			return DEFAULT_VALUES_FOR_PRIMITIVES.get(rawClass);

		} else {
			return null;
		}
	}

    public void mutate(final Object bean, final Object value) {
		try {

			mutator.invoke(bean, value);

		} catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw cannotSetPropertyValue(bean, e);
        }
	}

	// ------------------------------------------------------------------------

	private KasperQueryAdapterException cannotGetPropertyValue(final Exception e) {
		return new KasperQueryAdapterException(
				"Unable to get value of property " + name + " from bean "
						+ accessor.getDeclaringClass(), e);
	}

	private KasperQueryAdapterException cannotSetPropertyValue(final Object value, final Exception e) {
		return new KasperQueryAdapterException("Unable to set property " + name
				+ " from bean " + accessor.getDeclaringClass() + " with value "
				+ value, e);
	}

	// ------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		final PropertyAdapter other = (PropertyAdapter) obj;
		if (null == name) {
			if (null != other.name) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		return true;
	}

}
