// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.query.exposition;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.lang.System.arraycopy;

public final class DefaultTypeAdapters {

	private DefaultTypeAdapters() { /* singleton */
	}

	// ------------------------------------------------------------------------

	public static final ITypeAdapter<Integer> INT_ADAPTER = new ITypeAdapter<Integer>() {
		@Override
		public void adapt(final Integer value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Integer adapt(QueryParser parser) {
			return parser.intValue();
		}
	};

    // --

	public static final ITypeAdapter<Long> Long_ADAPTER = new ITypeAdapter<Long>() {
		@Override
		public void adapt(final Long value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Long adapt(final QueryParser parser) {
			return parser.longValue();
		}
	};

    // --

	public static final ITypeAdapter<Double> DOUBLE_ADAPTER = new ITypeAdapter<Double>() {
		@Override
		public void adapt(final Double value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Double adapt(final QueryParser parser) {
			return parser.doubleValue();
		}
	};

    // --

	public static final ITypeAdapter<Float> FLOAT_ADAPTER = new ITypeAdapter<Float>() {
		@Override
		public void adapt(final Float value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Float adapt(final QueryParser parser) {
			return Double.valueOf(parser.doubleValue()).floatValue();
		}
	};

    // --

	public static final ITypeAdapter<Short> SHORT_ADAPTER = new ITypeAdapter<Short>() {
		@Override
		public void adapt(final Short value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Short adapt(final QueryParser parser) {
			return Integer.valueOf(parser.intValue()).shortValue();
		}
	};

	// --

	public static final ITypeAdapter<String> STRING_ADAPTER = new ITypeAdapter<String>() {
		@Override
		public void adapt(final String value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public String adapt(final QueryParser parser) {
			return parser.value();
		}
	};

	// --

	public static final ITypeAdapter<Boolean> BOOLEAN_ADAPTER = new ITypeAdapter<Boolean>() {
		@Override
		public void adapt(final Boolean value, final QueryBuilder builder) {
			builder.add(value.toString());
		}

		@Override
		public Boolean adapt(final QueryParser parser) {
			return parser.booleanValue();
		}
	};

	// --

	public static final ITypeAdapter<Date> DATE_ADAPTER = new ITypeAdapter<Date>() {
		@Override
		public void adapt(final Date value, final QueryBuilder builder) {
			builder.add(String.valueOf(value.getTime()));
		}

		@Override
		public Date adapt(final QueryParser parser) {
			return new Date(parser.longValue());
		}
	};

	// --

	public static final ITypeAdapter<DateTime> DATETIME_ADAPTER = new ITypeAdapter<DateTime>() {
		@Override
		public void adapt(final DateTime value, final QueryBuilder builder) {
			builder.add(String.valueOf(value.getMillis()));
		}

		@Override
		public DateTime adapt(final QueryParser parser) {
			return new DateTime(parser.longValue());
		}
	};

	// --

	public static final ITypeAdapterFactory<Object> ARRAY_ADAPTER_FACTORY = new ITypeAdapterFactory<Object>() {
		@Override
		public Optional<ITypeAdapter<Object>> create(
				final TypeToken<Object> typeToken,
				final IQueryFactory adapterFactory) {
			final Class<?> rawClass = typeToken.getRawType();

			if (rawClass.isArray()) {
				final ITypeAdapter<?> elementAdapter = adapterFactory
						.create(TypeToken.of(rawClass.getComponentType()));

				@SuppressWarnings({ "unchecked" })
				final ITypeAdapter<Object> adapter = new ArrayAdapter(
						(ITypeAdapter<Object>) elementAdapter,
						rawClass.getComponentType());
				return Optional.fromNullable(adapter);
			}

			return Optional.absent();
		}
	};

	// --

	public static final class ArrayAdapter implements ITypeAdapter<Object> {
		private final ITypeAdapter<Object> componentAdapter;
		private final Class<?> componentClass;

		public ArrayAdapter(final ITypeAdapter<Object> componentAdapter, final Class<?> componentClass) {
			this.componentAdapter = componentAdapter;
			this.componentClass = componentClass;
		}

		@Override
		public void adapt(final Object array, final QueryBuilder builder) throws Exception {
			final int len = Array.getLength(array);

			for (int i = 0; i < len; i++) {
				final Object element = Array.get(array, i);
				componentAdapter.adapt(element, builder);
			}
		}

		@Override
		public Object adapt(final QueryParser parser) throws Exception {
			int size = 10;
			Object array = Array.newInstance(componentClass, size);
			int idx = 0;

			for (final QueryParser nextParser : parser) {
				if (idx >= size) {
					size = size * 2 + 1;
					array = expandArray(array, idx, size);
				}
				Array.set(array, idx++, componentAdapter.adapt(nextParser));
			}
			if (idx < size) {
				array = expandArray(array, idx, idx);
			}
			return array;
		}

		private Object expandArray(final Object array, final int len, final int size) {
			Object tmpArray = Array.newInstance(componentClass, size);
			arraycopy(array, 0, tmpArray, 0, len);
			return tmpArray;
		}
	}

	// --

	public static final ITypeAdapterFactory<Collection<?>> COLLECTION_ADAPTER_FACTORY = new ITypeAdapterFactory<Collection<?>>() {
		@Override
		public Optional<ITypeAdapter<Collection<?>>> create(
				final TypeToken<Collection<?>> typeToken,
				final IQueryFactory adapterFactory) {
			final Class<?> rawClass = typeToken.getRawType();

			if (Collection.class.isAssignableFrom(rawClass)) {

				final Class<?> elementType = ReflectionGenericsResolver
						.getParameterTypeFromClass(typeToken.getType(),
								Collection.class, 0).get();
				final ITypeAdapter<?> elementAdapter = adapterFactory
						.create(TypeToken.of(elementType));

				@SuppressWarnings({ "unchecked", "rawtypes" })
				final ITypeAdapter<Collection<?>> adapter = new CollectionAdapter(
						elementAdapter);

				return Optional.fromNullable(adapter);
			}

			return Optional.absent();
		}
	};

	// --

	public static final class CollectionAdapter<E> implements ITypeAdapter<Collection<E>> {
		private final ITypeAdapter<E> elementAdapter;

		CollectionAdapter(final ITypeAdapter<E> elementAdapter) {
			this.elementAdapter = elementAdapter;
		}

		@Override
		public void adapt(final Collection<E> value, final QueryBuilder builder) throws Exception {
			for (final E element : value) {
				elementAdapter.adapt(element, builder);
			}
		}

		public Collection<E> adapt(final QueryParser parser) throws Exception {
			final List<E> listOfE = new ArrayList<>();
			for (final QueryParser next : parser) {
				listOfE.add(elementAdapter.adapt(next));
            }
			return listOfE;
		}
	}

	public static class EnumAdapter<T extends Enum<T>> implements ITypeAdapter<T> {
		private final Class<T> eClass;

		public EnumAdapter(final Class<T> eClass) {
			this.eClass = eClass;
		}

		public void adapt(final T obj, final QueryBuilder builder) {
			builder.add(obj.name());
		}

		public T adapt(final QueryParser parser) {
			return Enum.valueOf(eClass, parser.value());
		}
	}

	public static final ITypeAdapterFactory<Enum<?>> ENUM_ADAPTER_FACTORY = new ITypeAdapterFactory<Enum<?>>() {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Optional<ITypeAdapter<Enum<?>>> create(
				TypeToken<Enum<?>> typeToken, IQueryFactory adapterFactory) {
			final Class<?> rawClass = typeToken.getRawType();
			final ITypeAdapter<Enum<?>> adapter;

			if (rawClass.isEnum() || Enum.class.isAssignableFrom(rawClass)) {
				adapter = new EnumAdapter(rawClass);
			} else {
				adapter = null;
			}

			return Optional.fromNullable(adapter);
		}
	};

}
