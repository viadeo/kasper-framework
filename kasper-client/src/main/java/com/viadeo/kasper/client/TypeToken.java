/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public abstract class TypeToken<T> {
    private final Type type;
    private final Class<T> rawClass;

    @SuppressWarnings("unchecked")
    protected TypeToken() {
        Type superType = getClass().getGenericSuperclass();
        if (superType instanceof Class<?>) {
            throw new IllegalArgumentException("You must specify the parametrized type!");
        }
        type = ((ParameterizedType) superType).getActualTypeArguments()[0];
        rawClass = ReflectionGenericsResolver.getClass(type).get();
    }

    private TypeToken(Class<T> rawClass, Type type) {
        this.type = type;
        this.rawClass = rawClass;
    }

    private final static ConcurrentHashMap<Type, TypeToken<?>> _genericTypesCache = new ConcurrentHashMap<Type, TypeToken<?>>();

    public static TypeToken<?> typeFor(Type type) {
        @SuppressWarnings("unchecked")
        Class<Object> typeRawClass = ReflectionGenericsResolver.getClass(type).get();
        @SuppressWarnings("unchecked")
        TypeToken<Object> genericType = (TypeToken<Object>) _genericTypesCache.get(type);
        if (genericType == null) {
            genericType = new TypeToken<Object>(typeRawClass, type) {};
            _genericTypesCache.put(type, genericType);
        }
        return genericType;
    }

    public static <T> TypeToken<T> typeFor(Class<T> rawClass) {
        @SuppressWarnings("unchecked")
        // FIXME need a check to ensure it is the right type ?
        TypeToken<T> genericType = (TypeToken<T>) _genericTypesCache.get(rawClass);
        if (genericType == null) {
            genericType = new TypeToken<T>(rawClass, rawClass) {};
            _genericTypesCache.put(rawClass, genericType);
        }
        return genericType;
    }

    public final Type getType() {
        return type;
    }

    public final Class<T> getRawClass() {
        return rawClass;
    }
}
