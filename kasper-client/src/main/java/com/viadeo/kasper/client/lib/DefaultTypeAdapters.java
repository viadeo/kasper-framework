// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public final class DefaultTypeAdapters {

    private DefaultTypeAdapters() { /* singleton */
    }

    // ------------------------------------------------------------------------

    public static final ITypeAdapter<Number> NUMBER_ADAPTER = new ITypeAdapter<Number>() {
        @Override
        public void adapt(final Number value, final QueryBuilder builder) {
            builder.add(value);
        }
    };

    // --

    public static final ITypeAdapter<String> STRING_ADAPTER = new ITypeAdapter<String>() {
        @Override
        public void adapt(final String value, final QueryBuilder builder) {
            builder.add(value);
        }
    };

    // --

    public static final ITypeAdapter<Boolean> BOOLEAN_ADAPTER = new ITypeAdapter<Boolean>() {
        @Override
        public void adapt(final Boolean value, final QueryBuilder builder) {
            builder.add(value.toString());
        }
    };

    // --

    public static final ITypeAdapter<Date> DATE_ADAPTER = new ITypeAdapter<Date>() {
        @Override
        public void adapt(final Date value, final QueryBuilder builder) {
            builder.add(String.valueOf(value.getTime()));
        }
    };

    // --

    public static final ITypeAdapter<DateTime> DATETIME_ADAPTER = new ITypeAdapter<DateTime>() {
        @Override
        public void adapt(final DateTime value, final QueryBuilder builder) {
            builder.add(String.valueOf(value.getMillis()));
        }
    };

    // --

    public static final ITypeAdapterFactory<Object> ARRAY_ADAPTER_FACTORY = new ITypeAdapterFactory<Object>() {
        @Override
        public Optional<ITypeAdapter<Object>> create(final TypeToken<Object> typeToken, final IQueryFactory adapterFactory) {
            final Class<?> rawClass = typeToken.getRawType();

            if (rawClass.isArray()) {
                final ITypeAdapter<?> elementAdapter = adapterFactory.create(TypeToken.of(rawClass.getComponentType()));

                @SuppressWarnings({ "unchecked", "rawtypes" })
                final ITypeAdapter<Object> adapter = new ArrayAdapter(elementAdapter);
                return Optional.fromNullable(adapter);
            }

            return Optional.absent();
        }
    };

    // --

    public static final class ArrayAdapter<C> implements ITypeAdapter<C[]> {
        private final ITypeAdapter<C> componentAdapter;

        public ArrayAdapter(final ITypeAdapter<C> componentAdapter) {
            this.componentAdapter = componentAdapter;
        }
        @Override
        public void adapt(final C[] value, final QueryBuilder builder) {
            for (final C component : value) {
                componentAdapter.adapt(component, builder);
            }
        }
    }

    // --

    public static final ITypeAdapterFactory<Collection<?>> COLLECTION_ADAPTER_FACTORY = new ITypeAdapterFactory<Collection<?>>() {
        @Override
        public Optional<ITypeAdapter<Collection<?>>> create(final TypeToken<Collection<?>> typeToken, final IQueryFactory adapterFactory) {
            final Class<?> rawClass = typeToken.getRawType();

            if (Collection.class.isAssignableFrom(rawClass)) {
                final Class<?> elementType = ReflectionGenericsResolver.getParameterTypeFromClass(typeToken.getType(), Collection.class, 0).get();
                final ITypeAdapter<?> elementAdapter = adapterFactory.create(TypeToken.of(elementType));

                @SuppressWarnings({ "unchecked", "rawtypes" })
                final ITypeAdapter<Collection<?>> adapter = new CollectionAdapter(elementAdapter);

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
        public void adapt(final Collection<E> value, final QueryBuilder builder) {
            for (final E element : value) {
                elementAdapter.adapt(element, builder);
            }
        }
    }

}
