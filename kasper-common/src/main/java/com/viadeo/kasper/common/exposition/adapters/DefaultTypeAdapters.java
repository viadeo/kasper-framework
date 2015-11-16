// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.adapters;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryFactory;
import com.viadeo.kasper.common.exposition.query.QueryParser;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;

public final class DefaultTypeAdapters {

    static final Integer PARSER_ARRAY_STARTING_SIZE = 10;

	private DefaultTypeAdapters() { /* singleton */ }

	// ------------------------------------------------------------------------

	public static final TypeAdapter<Integer> INT_ADAPTER = new TypeAdapter<Integer>() {
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

	public static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
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

	public static final TypeAdapter<Double> DOUBLE_ADAPTER = new TypeAdapter<Double>() {
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

	public static final TypeAdapter<Float> FLOAT_ADAPTER = new TypeAdapter<Float>() {
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

	public static final TypeAdapter<Short> SHORT_ADAPTER = new TypeAdapter<Short>() {
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

	public static final TypeAdapter<String> STRING_ADAPTER = new TypeAdapter<String>() {
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

	public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
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

	public static final TypeAdapter<Date> DATE_ADAPTER = new TypeAdapter<Date>() {
		@Override
		public void adapt(final Date value, final QueryBuilder builder) {
			builder.add(String.valueOf(value.getTime()));
		}

		@Override
		public Date adapt(final QueryParser parser) {
			return parseDate(parser).toDate();
		}
	};

	// --

	public static final TypeAdapter<DateTime> DATETIME_ADAPTER = new TypeAdapter<DateTime>() {
		@Override
		public void adapt(final DateTime value, final QueryBuilder builder) {
			builder.add(String.valueOf(value.getMillis()));
		}

		@Override
		public DateTime adapt(final QueryParser parser) {
			return parseDate(parser);
		}
	};
	
	private static DateTime parseDate(final QueryParser parser) {
	    try {
	        return new DateTime(parser.longValue());
	    } catch (final NumberFormatException nfe) {
	        return new DateTime(parser.value());
	    }
	}

	// --

	public static final TypeAdapterFactory<Object> ARRAY_ADAPTER_FACTORY = new TypeAdapterFactory<Object>() {
		@Override
		public Optional<TypeAdapter<Object>> create(
				final TypeToken<Object> typeToken,
				final QueryFactory adapterFactory) {
			final Class rawClass = typeToken.getRawType();

			if (rawClass.isArray()) {
                @SuppressWarnings({ "unchecked" })
                final TypeAdapter elementAdapter = adapterFactory
						.create(
                                TypeToken.of(rawClass.getComponentType())
                        );

				@SuppressWarnings({ "unchecked" })
				final TypeAdapter<Object> adapter = new ArrayAdapter(
						(TypeAdapter<Object>) elementAdapter,
                        rawClass.getComponentType()
                );
				return Optional.fromNullable(adapter);
			}

			return Optional.absent();
		}
	};

    // --

	public static final TypeAdapterFactory<Collection> COLLECTION_ADAPTER_FACTORY = new TypeAdapterFactory<Collection>() {
		@Override
		public Optional<TypeAdapter<Collection>> create(
				final TypeToken<Collection> typeToken,
				final QueryFactory adapterFactory) {
			final Class rawClass = typeToken.getRawType();

			if (Collection.class.isAssignableFrom(rawClass)) {

				final Class elementType = ReflectionGenericsResolver
						.getParameterTypeFromClass(
                            typeToken.getType(),
                            Collection.class, 0
                        ).get();

                @SuppressWarnings({ "unchecked" })
				final TypeAdapter elementAdapter = adapterFactory.create(TypeToken.of(elementType));

				@SuppressWarnings({ "unchecked", "rawtypes" })
				final TypeAdapter<Collection> adapter = new CollectionAdapter(elementAdapter);

				return Optional.fromNullable(adapter);
			}

			return Optional.absent();
		}
	};

	// --

    public static final TypeAdapterFactory<Enum> ENUM_ADAPTER_FACTORY = new TypeAdapterFactory<Enum>() {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Optional<TypeAdapter<Enum>> create(
				TypeToken<Enum> typeToken, QueryFactory adapterFactory) {
			final Class rawClass = typeToken.getRawType();
			final TypeAdapter<Enum> adapter;

			if (rawClass.isEnum() || Enum.class.isAssignableFrom(rawClass)) {
				adapter = new EnumAdapter(rawClass);
			} else {
				adapter = null;
			}

			return Optional.fromNullable(adapter);
		}
	};

	public static final TypeAdapter<KasperID> KASPERID_ADAPTER = new TypeAdapter<KasperID>() {
        @Override
        public void adapt(final KasperID value, final QueryBuilder builder) {
            builder.add(value.toString());
        }

        @Override
        public KasperID adapt(final QueryParser parser) {
            return new DefaultKasperId(parser.value());
        }
    };

}
