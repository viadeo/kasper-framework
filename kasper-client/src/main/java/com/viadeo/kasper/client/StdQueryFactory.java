/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.collect.ImmutableSet;
import com.viadeo.kasper.cqrs.query.IQuery;

class StdQueryFactory implements QueryFactory {
    private final ConcurrentHashMap<Type, TypeAdapter<?>> adapters;
    private final List<TypeAdapterFactory> factories;

    private final VisibilityFilter visibilityFilter;

    public StdQueryFactory(Map<Type, ? extends TypeAdapter<?>> adapters, List<? extends TypeAdapterFactory> factories, VisibilityFilter visibilityFilter) {
        this.visibilityFilter = checkNotNull(visibilityFilter);
        this.factories = new ArrayList<TypeAdapterFactory>(checkNotNull(factories));
        this.adapters = new ConcurrentHashMap<Type, TypeAdapter<?>>(checkNotNull(adapters));
    }

    @SuppressWarnings("unchecked")
    // the library is providing typesafety through TypeToken and reflection
    @Override
    public <T> TypeAdapter<T> create(TypeToken<T> typeToken) {
        // first lets check if a TypeAdapter is available for that class
        TypeAdapter<T> adapter = (TypeAdapter<T>) adapters.get(typeToken.getType());
        if (adapter == null) {
            for (TypeAdapterFactory factory : factories) {
                if ((adapter = factory.create(typeToken, this)) != null) {
                    break;
                }
            }

            if (adapter == null) {
                if (!IQuery.class.isAssignableFrom(typeToken.getRawClass())) {
                    throw new KasperClientException("Could not find any valid TypeAdapter for type " + typeToken.getRawClass());
                }
                adapter = (TypeAdapter<T>) provideBeanQueryMapper((Class<IQuery>) typeToken.getRawClass());
            }
            checkNotNull(adapter);
            adapters.putIfAbsent(typeToken.getType(), adapter);
        }

        return adapter;
    }

    private TypeAdapter<? extends IQuery> provideBeanQueryMapper(Class<? extends IQuery> queryClass) {
        Set<PropertyAdapter> adapters = new HashSet<PropertyAdapter>();
        // no need to look at interfaces as we are interested only in implementations
        for (Class<?> superClass = queryClass; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            Method[] methods = superClass.getDeclaredMethods();
            for (Method m : methods) {
                if (visibilityFilter.isVisible(m) && isAccessor(m)) {
                    @SuppressWarnings("unchecked")
                    TypeToken<Object> propertyType = (TypeToken<Object>) TypeToken.typeFor(m.getGenericReturnType());
                    TypeAdapter<Object> delegateAdapter = create(propertyType);
                    if (delegateAdapter != null) {
                        PropertyAdapter propertyAdapter = new PropertyAdapter(m, resolveName(m), delegateAdapter);
                        if (!adapters.contains(propertyAdapter)) {
                            adapters.add(propertyAdapter);
                        }
                    }
                    else {
                        throw new KasperClientException("Complex Queries are not supported! " +
                                "Please flatten your Pojo in order to contain only java literal properties or register custom a TypeAdapter.");
                    }
                }
            }
        }

        if (adapters.isEmpty()) {
            throw new KasperClientException("No property has been discovered for query " + queryClass);
        }

        return new BeanQueryMapper(adapters);
    }

    private String resolveName(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("is")) {
            return firstCharToLowerCase(methodName.substring(2));
        }
        if (methodName.startsWith("get")) {
            return firstCharToLowerCase(methodName.substring(3));
        }
        throw new IllegalStateException("Method must respect Java Bean conventions and start with is or get.");
    }
    
    private String firstCharToLowerCase(String str) {
        String newStr = str.substring(0, 1).toLowerCase();
        if (str.length() > 1) 
            return newStr + str.substring(1);
        else return newStr;
    }

    private boolean isAccessor(Method method) {
        if (method.getName().startsWith("get") && method.getName().length() > 3 && method.getParameterTypes().length == 0) {
            return true;
        }
        if (method.getName().startsWith("is") && method.getName().length() > 2 && method.getParameterTypes().length == 0) {
            return true;
        }
        return false;
    }

    private class PropertyAdapter extends TypeAdapter<Object> {
        private final Method accessor;
        private final String name;
        private final TypeAdapter<Object> adapter;

        public PropertyAdapter(Method accessor, String name, TypeAdapter<Object> adapter) {
            this.accessor = checkNotNull(accessor);
            this.name = checkNotNull(name);
            this.adapter = checkNotNull(adapter);
        }

        @Override
        public void adapt(Object bean, QueryBuilder builder) {
            try {
                Object value = accessor.invoke(bean);
                if (value != null) {
                    builder.begin(name);
                    adapter.adapt(value, builder);
                }
            }
            catch (IllegalArgumentException e) {
                throw _canNotGetPropertyValue(e);
            }
            catch (IllegalAccessException e) {
                throw _canNotGetPropertyValue(e);
            }
            catch (InvocationTargetException e) {
                throw _canNotGetPropertyValue(e);
            }
        }

        private KasperClientException _canNotGetPropertyValue(Exception e) {
            return new KasperClientException("Unable to get value of property " + name
                    + " from bean " + accessor.getDeclaringClass(), e);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PropertyAdapter other = (PropertyAdapter) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            }
            else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }
    }

    private class BeanQueryMapper extends TypeAdapter<IQuery> {
        private final Set<PropertyAdapter> adapters;

        public BeanQueryMapper(Set<PropertyAdapter> adapters) {
            this.adapters = ImmutableSet.copyOf(adapters);
        }

        @Override
        public void adapt(IQuery value, QueryBuilder builder) {
            for (PropertyAdapter adapter : adapters) {
                adapter.adapt(value, builder);
            }
        }
    }
}
