// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public abstract class TypeToken<T> {
    
    private final Type type;
    private final Class<T> rawClass;

    // ------------------------------------------------------------------------
    
    private static final ConcurrentMap<Type, TypeToken<?>> GENERIC_TYPES_CACHE = Maps.newConcurrentMap();
    
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    protected TypeToken() {
        final Type superType = getClass().getGenericSuperclass();
        if (superType instanceof Class<?>) {
            throw new IllegalArgumentException("You must specify the parametrized type!");
        }
        
        this.type = ((ParameterizedType) superType).getActualTypeArguments()[0];
        this.rawClass = ReflectionGenericsResolver.getClass(type).get();
    }

    private TypeToken(final Class<T> rawClass, final Type type) {
        this.type = type;
        this.rawClass = rawClass;
    }

    // ------------------------------------------------------------------------

    public static TypeToken<?> typeFor(final Type type) {
        @SuppressWarnings("unchecked")
        final Class<Object> typeRawClass = ReflectionGenericsResolver.getClass(type).get();
        
        @SuppressWarnings("unchecked")
        TypeToken<Object> genericType = (TypeToken<Object>) GENERIC_TYPES_CACHE.get(type);
        
        if (null == genericType) {
            genericType = new TypeToken<Object>(typeRawClass, type) {};
            GENERIC_TYPES_CACHE.put(type, genericType);
        }
        
        return genericType;
    }

    // --
    
    public static <T> TypeToken<T> typeFor(final Class<T> rawClass) {
        @SuppressWarnings("unchecked") // FIXME need a check to ensure it is the right type ?
        TypeToken<T> genericType = (TypeToken<T>) GENERIC_TYPES_CACHE.get(rawClass);
        
        if (null == genericType) {
            genericType = new TypeToken<T>(rawClass, rawClass) {};
            GENERIC_TYPES_CACHE.put(rawClass, genericType);
        }
        
        return genericType;
    }

    // ------------------------------------------------------------------------
    
    public final Type getType() {
        return type;
    }

    public final Class<T> getRawClass() {
        return rawClass;
    }
    
}
