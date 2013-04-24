/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.util.Collection;
import java.util.Date;
import org.joda.time.DateTime;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public class DefaultAdapters {
    final static TypeAdapter<Number> numberAdapter = new TypeAdapter<Number>() {
        @Override
        public void adapt(Number value, QueryBuilder builder) {
            builder.add(value.toString());
        }
    }.skipNull();

    final static TypeAdapter<String> stringAdapter = new TypeAdapter<String>() {
        @Override
        public void adapt(String value, QueryBuilder builder) {
            builder.add(value);
        }
    }.skipNull();

    final static TypeAdapter<Boolean> booleanAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void adapt(Boolean value, QueryBuilder builder) {
            builder.add(value.toString());
        }
    }.skipNull();

    final static TypeAdapter<Date> dateAdapter = new TypeAdapter<Date>() {
        @Override
        public void adapt(Date value, QueryBuilder builder) {
            builder.add(String.valueOf(value.getTime()));
        }
    }.skipNull();

    final static TypeAdapter<DateTime> dateTimeAdapter = new TypeAdapter<DateTime>() {
        @Override
        public void adapt(DateTime value, QueryBuilder builder) {
            builder.add(String.valueOf(value.getMillis()));
        }
    }.skipNull();

    final static TypeAdapterFactory arrayAdapterFactory = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(TypeToken<T> typeToken, QueryFactory adapterFactory) {
            Class<?> rawClass = typeToken.getRawClass();
            if (rawClass.isArray()) {
                final TypeAdapter<?> elementAdapter = adapterFactory.create(TypeToken.typeFor(rawClass.getComponentType()));
                @SuppressWarnings({ "unchecked", "rawtypes" })
                TypeAdapter<T> adapter = new ArrayAdapter(elementAdapter).skipNull();
                return adapter;
            }

            return null;
        }
    };

    final static class ArrayAdapter<C> extends TypeAdapter<C[]> {
        private final TypeAdapter<C> componentAdapter;

        public ArrayAdapter(TypeAdapter<C> componentAdapter) {
            this.componentAdapter = componentAdapter;
        }

        @Override
        public void adapt(C[] value, QueryBuilder builder) {
            for (C component : value) {
                componentAdapter.adapt(component, builder);
            }
        }
    }

    final static TypeAdapterFactory collectionAdapterFactory = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(TypeToken<T> typeToken, QueryFactory adapterFactory) {
            Class<?> rawClass = typeToken.getRawClass();
            if (Collection.class.isAssignableFrom(rawClass)) {
                Class<?> elementType = ReflectionGenericsResolver.getParameterTypeFromClass(typeToken.getType(), Collection.class, 0).get();
                final TypeAdapter<?> elementAdapter = adapterFactory.create(TypeToken.typeFor(elementType));
                @SuppressWarnings({ "unchecked", "rawtypes" })
                TypeAdapter<T> adapter = new CollectionAdapter(rawClass, elementAdapter);
                return adapter;
            }

            return null;
        }
    };

    final static class CollectionAdapter<E> extends TypeAdapter<Collection<E>> {
        private final TypeAdapter<E> elementAdapter;

        CollectionAdapter(final Class<E> elementClass, final TypeAdapter<E> elementAdapter) {
            this.elementAdapter = elementAdapter;
        }

        @Override
        public void adapt(Collection<E> value, QueryBuilder builder) {
            for (E element : value) {
                elementAdapter.adapt(element, builder);
            }
        }
    }
}
