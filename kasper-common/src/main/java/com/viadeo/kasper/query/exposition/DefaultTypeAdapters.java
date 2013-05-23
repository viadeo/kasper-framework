// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.query.exposition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

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

	public static final ITypeAdapter<Long> Long_ADAPTER = new ITypeAdapter<Long>() {
		@Override
		public void adapt(final Long value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Long adapt(QueryParser parser) {
			return parser.longValue();
		}
	};

	public static final ITypeAdapter<Double> DOUBLE_ADAPTER = new ITypeAdapter<Double>() {
		@Override
		public void adapt(final Double value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Double adapt(QueryParser parser) {
			return parser.doubleValue();
		}
	};

	public static final ITypeAdapter<Float> FLOAT_ADAPTER = new ITypeAdapter<Float>() {
		@Override
		public void adapt(final Float value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Float adapt(QueryParser parser) {
			return Double.valueOf(parser.doubleValue()).floatValue();
		}
	};

	public static final ITypeAdapter<Short> SHORT_ADAPTER = new ITypeAdapter<Short>() {
		@Override
		public void adapt(final Short value, final QueryBuilder builder) {
			builder.add(value);
		}

		@Override
		public Short adapt(QueryParser parser) {
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
		public String adapt(QueryParser parser) {
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
		public Boolean adapt(QueryParser parser) {
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
		public Date adapt(QueryParser parser) {
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
		public DateTime adapt(QueryParser parser) {
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

				@SuppressWarnings({ "unchecked", "rawtypes" })
				final ITypeAdapter<Object> adapter = new ArrayAdapter(
						elementAdapter, rawClass.getComponentType());
				return Optional.fromNullable(adapter);
			}

			return Optional.absent();
		}
	};

	// --

	public static final class ArrayAdapter<C> implements ITypeAdapter<C[]> {
		private final ITypeAdapter<C> componentAdapter;
		private final Class<C> componentClass;

		public ArrayAdapter(final ITypeAdapter<C> componentAdapter,
				final Class<C> componentClass) {
			this.componentAdapter = componentAdapter;
			this.componentClass = componentClass;
		}

		@Override
		public void adapt(final C[] value, final QueryBuilder builder) {
			for (final C component : value) {
				componentAdapter.adapt(component, builder);
			}
		}

		@Override
		public C[] adapt(QueryParser parser) {
			int size = 10;
			Object array = Array.newInstance(componentClass, size);
			int idx = 0;
			for (QueryParser nextParser : parser) {
				if (idx >= size) {
					size = size * 2 + 1;
					array = expandArray(array, idx, size);
				}
				Array.set(array, idx++, componentAdapter.adapt(nextParser));
			}
			if (idx < size) {
				array = expandArray(array, idx, idx);
			}
			return null;
		}

		private Object expandArray(Object array, int len, int size) {
			Object tmpArray = Array.newInstance(componentClass, size);
			System.arraycopy(array, 0, tmpArray, 0, len);
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

	public static final class CollectionAdapter<E> implements
			ITypeAdapter<Collection<E>> {
		private final ITypeAdapter<E> elementAdapter;

		CollectionAdapter(final ITypeAdapter<E> elementAdapter) {
			this.elementAdapter = elementAdapter;
		}

		@Override
		public void adapt(final Collection<E> value, final QueryBuilder builder) {
			for (final E element : value) {
				elementAdapter.adapt(element, builder);
			}
		}

		public Collection<E> adapt(QueryParser parser) {
			List<E> listOfE = new ArrayList<E>();
			for (QueryParser next : parser)
				listOfE.add(elementAdapter.adapt(next));
			return listOfE;
		}
	}

	public static class EnumAdapter<T extends Enum<T>> implements
			ITypeAdapter<T> {
		private final Class<T> eClass;

		public EnumAdapter(Class<T> eClass) {
			this.eClass = eClass;
		}

		public void adapt(T obj, QueryBuilder builder) {
			builder.add(obj.name());
		}

		public T adapt(QueryParser parser) {
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
