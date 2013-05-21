// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.cqrs.query.IQuery;

/**
 * This class is responsible of locating and doing all the wiring between TypeAdapters.
 * You can use it in your custom {@link ITypeAdapterFactory} in order to delegate the "serialization" to existing mechanism.
 * 
 * @see TypeAdapter
 * @see ITypeAdapterFactory
 */
public class StdQueryFactory implements IQueryFactory {

    private static final String PREFIX_METHOD_IS = "is";
    private static final int PREFIX_METHOD_IS_LEN = 2;
    private static final String PREFIX_METHOD_GET = "get";
    private static final int PREFIX_METHOD_GET_LEN = 3;

    private final ConcurrentMap<Type, ITypeAdapter<?>> adapters;
    private final List<ITypeAdapterFactory<?>> factories;

    private final VisibilityFilter visibilityFilter;

    // ------------------------------------------------------------------------

    public StdQueryFactory(final Map<Type, ? extends ITypeAdapter<?>> adapters,
            final List<? extends ITypeAdapterFactory<?>> factories,
            final VisibilityFilter visibilityFilter) {
        this.visibilityFilter = checkNotNull(visibilityFilter);
        this.factories = Lists.newArrayList(checkNotNull(factories));

        this.adapters = Maps.newConcurrentMap();
        this.adapters.putAll(checkNotNull(adapters));
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // Safe: the library is providing type-safety 
    // through TypeToken and reflection
    @Override
    public <T> ITypeAdapter<T> create(final TypeToken<T> typeToken) {

        //- first lets check if a TypeAdapter is available for that class
        ITypeAdapter<T> adapter = (ITypeAdapter<T>) adapters.get(typeToken.getType());

        if (null == adapter) {
            for (final ITypeAdapterFactory<?> candidateFactory : factories) {
                if (TypeToken.of(candidateFactory.getClass()).resolveType(ITypeAdapterFactory.class.getTypeParameters()[0]).isAssignableFrom(typeToken)) {
                    ITypeAdapterFactory<T> factory = (ITypeAdapterFactory<T>) candidateFactory;
                    final Optional<ITypeAdapter<T>> adapterOpt = factory.create(typeToken, this);
                    if (adapterOpt.isPresent()) {
                        adapter = adapterOpt.get();
                        break;
                    }
                }
            }

            if (null == adapter) {
                if (!IQuery.class.isAssignableFrom(typeToken.getRawType())) {
                    throw new KasperClientException("Could not find any valid TypeAdapter for type " + typeToken.getRawType());
                }
                adapter = (ITypeAdapter<T>) provideBeanQueryMapper((TypeToken<Class<? extends IQuery>>) typeToken);
            }
            checkNotNull(adapter);
            adapter = new NullSafeTypeAdapter<T>(adapter);
            adapters.putIfAbsent(typeToken.getType(), adapter);
        }

        return adapter;
    }

    // ------------------------------------------------------------------------

    private ITypeAdapter<? extends IQuery> provideBeanQueryMapper(final TypeToken<Class<? extends IQuery>> typeToken) {
        final Set<PropertyAdapter> retAdapters = Sets.newHashSet();

        // no need to look at interfaces as we are interested only in implementations
        Class<?> superClass = typeToken.getRawType();
        while ((null != superClass) && !superClass.equals(Object.class)) {
            final Method[] methods = superClass.getDeclaredMethods();
            for (final Method m : methods) {
                if (visibilityFilter.isVisible(m) && isAccessor(m)) {
                    @SuppressWarnings("unchecked")
                    final TypeToken<Object> propertyType = (TypeToken<Object>) typeToken.resolveType(m.getGenericReturnType());
                    final ITypeAdapter<Object> delegateAdapter = create(propertyType);

                    if (null != delegateAdapter) {
                        final PropertyAdapter propertyAdapter = new PropertyAdapter(m, resolveName(m), delegateAdapter);
                        if (!retAdapters.contains(propertyAdapter)) {
                            retAdapters.add(propertyAdapter);
                        }
                    }
                    else {
                        throw new KasperClientException("Complex Queries are not supported! " +
                                "Please flatten your Pojo in order to contain only java literal " +
                                "properties or register custom a TypeAdapter.");
                    }
                }
            }

            superClass = superClass.getSuperclass();
        }

        if (retAdapters.isEmpty()) {
            throw new KasperClientException("No property has been discovered for query " + typeToken.getRawType());
        }

        return new BeanQueryMapper(retAdapters);
    }

    // ------------------------------------------------------------------------

    private String resolveName(final Method method) {
        final String methodName = method.getName();

        if (methodName.startsWith(PREFIX_METHOD_IS)) {
            return firstCharToLowerCase(methodName.substring(PREFIX_METHOD_IS_LEN));
        }

        if (methodName.startsWith(PREFIX_METHOD_GET)) {
            return firstCharToLowerCase(methodName.substring(PREFIX_METHOD_GET_LEN));
        }

        throw new IllegalStateException("Method must respect Java Bean conventions and start with is or get.");
    }

    // --

    private String firstCharToLowerCase(final String str) {
        final String newStr = str.substring(0, 1).toLowerCase();

        if (str.length() > 1) {
            return newStr + str.substring(1);
        }
        else {
            return newStr;
        }
    }

    // --

    private boolean isAccessor(final Method method) {

        if (method.getName().startsWith(PREFIX_METHOD_GET)
                && (method.getName().length() > PREFIX_METHOD_GET_LEN)
                && method.getParameterTypes().length == 0) {
            return true;
        }

        if (method.getName().startsWith(PREFIX_METHOD_IS)
                && method.getName().length() > PREFIX_METHOD_IS_LEN
                && method.getParameterTypes().length == 0) {
            return true;
        }

        return false;
    }

    // ------------------------------------------------------------------------

    private class BeanQueryMapper implements ITypeAdapter<IQuery> {
        private final Set<PropertyAdapter> adapters;

        public BeanQueryMapper(final Set<PropertyAdapter> adapters) {
            this.adapters = ImmutableSet.copyOf(adapters);
        }
        @Override
        public void adapt(final IQuery value, final QueryBuilder builder) {
            for (final PropertyAdapter adapter : adapters) {
                adapter.adapt(value, builder);
            }
        }
    }
}
