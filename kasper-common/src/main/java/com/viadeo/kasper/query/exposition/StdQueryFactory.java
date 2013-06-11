// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.thoughtworks.paranamer.*;
import com.viadeo.kasper.cqrs.query.IQuery;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is responsible of locating and doing all the wiring between TypeAdapters. You can use it in your custom
 * {@link ITypeAdapterFactory} in order to delegate the "serialization" to existing mechanism.
 * 
 * @see ITypeAdapter
 * @see ITypeAdapterFactory
 */
public class StdQueryFactory implements IQueryFactory {

    private static final String PREFIX_METHOD_IS = "is";
    private static final int PREFIX_METHOD_IS_LEN = 2;
    private static final String PREFIX_METHOD_GET = "get";
    private static final int PREFIX_METHOD_GET_LEN = 3;

    private static final String PREFIX_METHOD_SET = "set";
    private static final int PREFIX_METHOD_SET_LEN = 3;

    private static final Comparator<Constructor<?>> LEAST_PARAM_COUNT_CTR_COMPARATOR = new Comparator<Constructor<?>>() {
        @Override
        public int compare(final Constructor<?> o1, final Constructor<?> o2) {
            return o1.getParameterTypes().length - o2.getParameterTypes().length;
        }
    };

    private final ConcurrentMap<Type, ITypeAdapter<?>> adapters;
    private final Map<Type, BeanAdapter<?>> beanAdapters;
    private final List<ITypeAdapterFactory<?>> factories;

    private final VisibilityFilter visibilityFilter;
    private final Paranamer paranamer = new CachingParanamer(
                                          new AdaptiveParanamer(
                                            new AnnotationParanamer(new DefaultParanamer()), 
                                            new BytecodeReadingParanamer()
                                          )
                                        );

    // ------------------------------------------------------------------------

    public StdQueryFactory(final Map<Type, ITypeAdapter<?>> adapters,
                           final Map<Type, BeanAdapter<?>> beanAdapters,
                           final List<? extends ITypeAdapterFactory<?>> factories, 
                           final VisibilityFilter visibilityFilter) {

        this.visibilityFilter = checkNotNull(visibilityFilter);
        this.factories = Lists.newArrayList(checkNotNull(factories));
        
        this.beanAdapters = Maps.newHashMap(checkNotNull(beanAdapters));

        this.adapters = Maps.newConcurrentMap();
        this.adapters.putAll(checkNotNull(adapters));
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // Safe: the library is providing type-safety
    // through TypeToken and reflection
    @Override
    public <T> ITypeAdapter<T> create(final TypeToken<T> typeToken) {

        // - first lets check if a TypeAdapter is available for that class
        ITypeAdapter<T> adapter = (ITypeAdapter<T>) adapters.get(typeToken.getType());

        if (null == adapter) {
            for (final ITypeAdapterFactory<?> candidateFactory : factories) {
                if (TypeToken.of(candidateFactory.getClass())
                        .resolveType(ITypeAdapterFactory.class.getTypeParameters()[0])
                        .isAssignableFrom(typeToken)) {

                    final ITypeAdapterFactory<T> factory = (ITypeAdapterFactory<T>) candidateFactory;
                    final Optional<ITypeAdapter<T>> adapterOpt = factory.create(typeToken, this);

                    if (adapterOpt.isPresent()) {
                        adapter = adapterOpt.get();
                        break;
                    }
                }
            }

            if (null == adapter) {
                if (!IQuery.class.isAssignableFrom(typeToken.getRawType())) {
                    throw new KasperQueryAdapterException("Could not find any valid TypeAdapter for type "
                            + typeToken.getRawType());
                }
                adapter = (ITypeAdapter<T>) provideBeanQueryMapper((TypeToken<Class<? extends IQuery>>) typeToken);
            }
            checkNotNull(adapter);

            adapter = new NullSafeTypeAdapter<>(adapter);
            adapters.putIfAbsent(typeToken.getType(), adapter);
        }

        return adapter;
    }

    // ------------------------------------------------------------------------

    private ITypeAdapter<? extends IQuery> provideBeanQueryMapper(final TypeToken<Class<? extends IQuery>> typeToken) {

        final Set<PropertyAdapter> retAdapters = Sets.newHashSet();

        Map<String, Method> accessors = new HashMap<>();
        Map<String, Method> mutators = new HashMap<>();

        // no need to look at interfaces as we are interested only in
        // implementations
        Class<?> superClass = typeToken.getRawType();
        while ((null != superClass) && !superClass.equals(Object.class)) {
            collectAccessors(superClass, accessors);
            collectMutators(superClass, mutators);
            superClass = superClass.getSuperclass();
        }

        final BeanConstructor creator = resolveBeanConstructor(typeToken.getRawType());

        // now we need to create the PropertyAdapters
        /*
         * !!! We must handle the case of properties that have an accessor but
         * have no mutator or ctr argument. Lets throw an exception if all
         * properties with a mutator are not covered by an accessor or a ctr
         * param (consider only selected ctr, the others don't matter as we will
         * not use them)!!!
         */
        for (final Map.Entry<String, Method> accessorEntry : accessors.entrySet()) {
            @SuppressWarnings("unchecked")
            final TypeToken<Object> accessorType = (TypeToken<Object>) typeToken.resolveType(accessorEntry.getValue()
                    .getGenericReturnType());

            final Method mutator = mutators.get(accessorEntry.getKey());
            final BeanConstructorProperty ctrProperty = creator.parameters.get(accessorEntry.getKey());

            PropertyAdapter propertyAdapter;

            // we have a ctr with args, this property will be set using the ctr
            // => do not use the mutator
            if (null != ctrProperty) {
                final TypeToken<?> ctrTypeToken = typeToken.resolveType(ctrProperty.type);

                // FIXME do we want to check it or be more permissive?
                if (!accessorType.equals(ctrTypeToken)) {
                    throw new KasperQueryAdapterException("Type of parameter[" + ctrProperty.name + "] and accessor "
                            + accessorEntry.getValue().getName() + " in " + typeToken.getRawType().getName()
                            + " do not match.");
                }

                propertyAdapter = createPropertyAdapter(mutator, accessorEntry.getValue(),
                                                        ctrProperty.name, accessorType);

                if (!retAdapters.contains(propertyAdapter)) {
                    retAdapters.add(propertyAdapter);
                }

            } else {

                if (null != mutator) {

                    final TypeToken<?> mutatorTypeToken = typeToken.resolveType(mutator.getGenericParameterTypes()[0]);

                    // FIXME do we want to check it or be more permissive?
                    if (!accessorType.equals(mutatorTypeToken)) {
                        throw new KasperQueryAdapterException("Type of mutator[" + mutator.getName()
                                + "] and accessor " + accessorEntry.getValue().getName() + " in "
                                + typeToken.getRawType().getName() + " do not match.");
                    }

                    propertyAdapter = createPropertyAdapter(mutator, accessorEntry.getValue(),
                                                            accessorEntry.getKey(), accessorType);

                    if (!retAdapters.contains(propertyAdapter)) {
                        retAdapters.add(propertyAdapter);
                    }

                }
                // else {
                /*
                 * for the moment just lets ignore silently methods that have a
                 * set method and no get, and the inverse
                 */
                // }
            }
        }

        if (retAdapters.isEmpty()) {
            throw new KasperQueryAdapterException("No property has been discovered for query " + typeToken.getRawType());
        }

        return new BeanQueryMapper(creator, retAdapters);
    }

    // ------------------------------------------------------------------------

    private PropertyAdapter createPropertyAdapter(final Method mutator, final Method accessor, final String name,
            final TypeToken<Object> propertyType) {

        // for the moment lets mix accessor and mutator annotations as things must be symetric, latter if we need to
        // support more complex cases we will change things
        final Annotation[] propertyAnnotations;
        
        // Need to check for null if the property does not have a mutator but uses the ctr.
        // We could do it differently but its fine like that for the moment
        // I prefer avoiding to add more and more classes until we really need it
        // deleting code is harder than writing!
        if (mutator == null) propertyAnnotations = accessor.getAnnotations();
        else propertyAnnotations =  ObjectArrays.concat(mutator.getAnnotations(), accessor.getAnnotations(), Annotation.class);
        
        final BeanProperty property = new BeanProperty(name, accessor.getDeclaringClass(), propertyAnnotations, propertyType);
        
        final boolean handleName;
        final ITypeAdapter<Object> delegateAdapter;
        
        @SuppressWarnings("unchecked") // type safety guaranteed by the lib
        final BeanAdapter<Object> beanAdapter = (BeanAdapter<Object>) beanAdapters.get(property.getTypeToken().getType());
        
        if (beanAdapter == null) {
            handleName = true;
            delegateAdapter = create(propertyType);
        } else {
            handleName = false;
            delegateAdapter = new NullSafeTypeAdapter<>(new DecoratedBeanAdapter<>(property, beanAdapter));
        }
       
        if (null != delegateAdapter) {

            return new PropertyAdapter(property, accessor, mutator, delegateAdapter, handleName);

        } else {
            throw new KasperQueryAdapterException("Complex Queries are not supported! "
                    + "Please flatten your Pojo in order to contain only java literal "
                    + "properties or register custom a TypeAdapter.");
        }
    }
    
    // ------------------------------------------------------------------------

    private BeanConstructor resolveBeanConstructor(final Class<?> forClass) {
        final Constructor<?>[] ctrs = forClass.getDeclaredConstructors();

        /*
         * we want the no arg ctr on top
         */
        Arrays.sort(ctrs, LEAST_PARAM_COUNT_CTR_COMPARATOR);

        /*
         * we choose the first one, because it is crazy to try to guess which is
         * better to use
         * so the rule is choose the ctr with the less number of params
         */
        @SuppressWarnings("unchecked")
        final Constructor<Object> ctr = (Constructor<Object>) ctrs[0];

        /*
         * we now must resolve the parameter names
         */
        final String[] names = paranamer.lookupParameterNames(ctr);
        if (names.length != ctr.getParameterTypes().length) {
            throw new KasperQueryAdapterException("Could not resolve constructor[" + ctr + "] parameter names");
        }

        final Map<String, BeanConstructorProperty> parameters = new HashMap<>();

        for (int i = 0; i < names.length; i++) {
            parameters.put(
                    names[i],
                    new BeanConstructorProperty(i, ctr.getParameterAnnotations()[i], names[i], ctr
                            .getGenericParameterTypes()[i]));
        }

        return new BeanConstructor(ctr, parameters);
    }

    // ------------------------------------------------------------------------

    private void collectAccessors(final Class<?> fromClass, final Map<String, Method> accessors) {
        final Method[] methods = fromClass.getDeclaredMethods();

        for (final Method m : methods) {
            if (visibilityFilter.isVisible(m) && isAccessor(m)) {
                final String name = resolveName(m);
                if (!accessors.containsKey(name)) {
                    accessors.put(name, m);
                }
            }
        }
    }

    // ------------------------------------------------------------------------

    private void collectMutators(final Class<?> fromClass, final Map<String, Method> mutators) {
        final Method[] methods = fromClass.getDeclaredMethods();

        for (final Method m : methods) {
            if (visibilityFilter.isVisible(m) && isMutator(m)) {
                final String name = resolveName(m);
                if (!mutators.containsKey(name)) {
                    mutators.put(name, m);
                }
            }
        }
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

        if (methodName.startsWith(PREFIX_METHOD_SET)) {
            return firstCharToLowerCase(methodName.substring(PREFIX_METHOD_SET_LEN));
        }

        throw new IllegalStateException("Method must respect Java Bean conventions and start with is, get or set.");
    }

    // --

    private String firstCharToLowerCase(final String str) {
        final String newStr = str.substring(0, 1).toLowerCase();

        if (str.length() > 1) {
            return newStr + str.substring(1);
        } else {
            return newStr;
        }
    }

    // --

    private boolean isAccessor(final Method method) {

        return method.getName().startsWith(PREFIX_METHOD_GET) && (method.getName().length() > PREFIX_METHOD_GET_LEN)
                && method.getParameterTypes().length == 0 || method.getName().startsWith(PREFIX_METHOD_IS)
                && method.getName().length() > PREFIX_METHOD_IS_LEN && method.getParameterTypes().length == 0;
    }

    // --

    private boolean isMutator(final Method method) {

        return method.getName().startsWith(PREFIX_METHOD_SET) && (method.getName().length() > PREFIX_METHOD_SET_LEN)
                && method.getParameterTypes().length == 1;

    }

    // ------------------------------------------------------------------------

    private class BeanQueryMapper implements ITypeAdapter<IQuery> {
        private final Set<PropertyAdapter> adapters;
        private final BeanConstructor queryCtr;

        public BeanQueryMapper(final BeanConstructor queryCtr, final Set<PropertyAdapter> adapters) {
            this.adapters = ImmutableSet.copyOf(adapters);
            this.queryCtr = queryCtr;
        }

        @Override
        public void adapt(final IQuery value, final QueryBuilder builder) throws Exception {
            for (final PropertyAdapter adapter : adapters) {
                adapter.adapt(value, builder);
            }
        }

        @Override
        public IQuery adapt(final QueryParser parser) throws Exception {
            final Object[] ctrParams = new Object[queryCtr.parameters.size()];
            final List<Pair<PropertyAdapter, Object>> valuesToSet = new ArrayList<>();

            for (final PropertyAdapter adapter : adapters) {
                /* we have to check if the property exists in th sream
                * if it doesn't we should not override it in case of setters 
                * (for the ctr we have no choice as we can't pass null to primitive args)
                * */
                final boolean exists = adapter.existsInQuery(parser);
                final Object value = adapter.adapt(parser);
                final BeanConstructorProperty ctrParam = queryCtr.parameters.get(adapter.getName());

                if (ctrParam != null) {
                    ctrParams[ctrParam.position] = value;
                } else {
                    if (exists) {
                        valuesToSet.add(new Pair<>(adapter, value));
                    }
                }
            }

            final Object queryInstance = queryCtr.create(ctrParams);
            for (final Pair<PropertyAdapter, Object> pair : valuesToSet) {
                pair.firstValue.mutate(queryInstance, pair.secondValue);
            }

            return (IQuery) queryInstance;
        }
    }

    // --

    private class Pair<F, S> {
        private F firstValue;
        private S secondValue;

        public Pair(final F firstValue, final S secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }
    }

    // --

    private class BeanConstructor {
        private final Constructor<Object> ctr;
        private final Map<String, BeanConstructorProperty> parameters;

        public BeanConstructor(final Constructor<Object> ctr, final Map<String, BeanConstructorProperty> parameters) {
            this.ctr = ctr;
            this.parameters = parameters;
        }

        public Object create(final Object[] params) {
            try {

                return ctr.newInstance(params);

            } catch (final IllegalArgumentException | InstantiationException |
                           IllegalAccessException | InvocationTargetException e) {
                throw couldNotInstanciateQuery(e);
            }
        }

        private KasperQueryAdapterException couldNotInstanciateQuery(final Exception e) {
            return new KasperQueryAdapterException("Failed to instanciate query of type " + ctr.getDeclaringClass(), e);
        }
    }

    // --

    private class BeanConstructorProperty {
        private final int position;
        @SuppressWarnings("unused")
        private final Annotation[] annotations;
        private final String name;
        private final Type type;

        public BeanConstructorProperty(final int position, final Annotation[] annotations, final String name,
                final Type type) {
            this.position = position;
            this.annotations = annotations;
            this.name = name;
            this.type = type;
        }
    }

    // in fact it is a beanadapter adapted to ITypeAdapter, but naming it AdaptedBeanAdapter 
    // would sound lolish...(a la spring) :p, this class allows us benefit from what has been done for TypeAdapters
    static class DecoratedBeanAdapter<T> implements ITypeAdapter<T> {
        private final BeanProperty property;
        private final BeanAdapter<T> adapter;

        public DecoratedBeanAdapter(BeanProperty property, BeanAdapter<T> adapter) {
            this.property = property;
            this.adapter = adapter;
        }

        @Override
        public void adapt(T value, QueryBuilder builder) throws Exception {
            adapter.adapt(value, builder, property);
        }

        @Override
        public T adapt(QueryParser parser) throws Exception {
            return adapter.adapt(parser, property);
        }
    }

}
