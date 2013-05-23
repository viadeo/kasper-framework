// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.query.exposition;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.DefaultParanamer;
import com.thoughtworks.paranamer.Paranamer;
import com.viadeo.kasper.cqrs.query.IQuery;

/**
 * This class is responsible of locating and doing all the wiring between
 * TypeAdapters. You can use it in your custom {@link ITypeAdapterFactory} in
 * order to delegate the "serialization" to existing mechanism.
 * 
 * @see TypeAdapter
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
		public int compare(Constructor<?> o1, Constructor<?> o2) {
			return o1.getParameterTypes().length
					- o2.getParameterTypes().length;
		}
	};

	private final ConcurrentMap<Type, ITypeAdapter<?>> adapters;
	private final List<ITypeAdapterFactory<?>> factories;

	private final VisibilityFilter visibilityFilter;
	private final Paranamer paranamer = new CachingParanamer(
			new AdaptiveParanamer(new AnnotationParanamer(
					new DefaultParanamer()), new BytecodeReadingParanamer()));

	// ------------------------------------------------------------------------

	public StdQueryFactory(final Map<Type, ITypeAdapter<?>> adapters,
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

		// - first lets check if a TypeAdapter is available for that class
		ITypeAdapter<T> adapter = (ITypeAdapter<T>) adapters.get(typeToken
				.getType());

		if (null == adapter) {
			for (final ITypeAdapterFactory<?> candidateFactory : factories) {
				if (TypeToken
						.of(candidateFactory.getClass())
						.resolveType(
								ITypeAdapterFactory.class.getTypeParameters()[0])
						.isAssignableFrom(typeToken)) {
					ITypeAdapterFactory<T> factory = (ITypeAdapterFactory<T>) candidateFactory;
					final Optional<ITypeAdapter<T>> adapterOpt = factory
							.create(typeToken, this);
					if (adapterOpt.isPresent()) {
						adapter = adapterOpt.get();
						break;
					}
				}
			}

			if (null == adapter) {
				if (!IQuery.class.isAssignableFrom(typeToken.getRawType())) {
					throw new KasperQueryAdapterException(
							"Could not find any valid TypeAdapter for type "
									+ typeToken.getRawType());
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

	private ITypeAdapter<? extends IQuery> provideBeanQueryMapper(
			final TypeToken<Class<? extends IQuery>> typeToken) {
		final Set<PropertyAdapter> retAdapters = Sets.newHashSet();

		Map<String, Method> accessors = new HashMap<String, Method>();
		Map<String, Method> mutators = new HashMap<String, Method>();

		// no need to look at interfaces as we are interested only in
		// implementations
		Class<?> superClass = typeToken.getRawType();
		while ((null != superClass) && !superClass.equals(Object.class)) {
			collectAccessors(superClass, accessors);
			collectMutators(superClass, mutators);
			superClass = superClass.getSuperclass();
		}

		BeanConstructor creator = resolveBeanConstructor(typeToken.getRawType());

		// now we need to create the PropertyAdapters
		/*
		 * !!! We must handle the case of properties that have an accessor but
		 * have no mutator or ctr argument. Lets throw an exception if all
		 * properties with a mutator are not covered by an accessor or a ctr
		 * param (consider only selected ctr, the others don't matter as we will
		 * not use them)!!!
		 */
		for (Map.Entry<String, Method> accessorEntry : accessors.entrySet()) {
			@SuppressWarnings("unchecked")
			final TypeToken<Object> accessorType = (TypeToken<Object>) typeToken
					.resolveType(accessorEntry.getValue()
							.getGenericReturnType());

			Method mutator = mutators.get(accessorEntry.getKey());

			BeanConstructorProperty ctrProperty = creator.parameters
					.get(accessorEntry.getKey());

			PropertyAdapter propertyAdapter = null;

			// we have a ctr with args, this property will be set using the ctr
			// => do not use the mutator
			if (ctrProperty != null) {
				TypeToken<?> ctrTypeToken = typeToken
						.resolveType(ctrProperty.type);

				// FIXME do we want to check it or be more permissive?
				if (!accessorType.equals(ctrTypeToken))
					throw new KasperQueryAdapterException("Type of parameter["
							+ ctrProperty.name + "] and accessor "
							+ accessorEntry.getValue().getName() + " in "
							+ typeToken.getRawType().getName()
							+ " do not match.");

				propertyAdapter = createPropertyAdapter(mutator,
						accessorEntry.getValue(), ctrProperty.name,
						accessorType);
				if (!retAdapters.contains(propertyAdapter)) {
					retAdapters.add(propertyAdapter);
				}
			} else if (mutator != null) {
				TypeToken<?> mutatorTypeToken = typeToken.resolveType(mutator
						.getGenericParameterTypes()[0]);

				// FIXME do we want to check it or be more permissive?
				if (!accessorType.equals(mutatorTypeToken))
					throw new KasperQueryAdapterException("Type of mutator["
							+ mutator.getName() + "] and accessor "
							+ accessorEntry.getValue().getName() + " in "
							+ typeToken.getRawType().getName()
							+ " do not match.");

				propertyAdapter = createPropertyAdapter(mutator,
						accessorEntry.getValue(), accessorEntry.getKey(),
						accessorType);
				if (!retAdapters.contains(propertyAdapter)) {
					retAdapters.add(propertyAdapter);
				}
			} else {
				// for the moment just lets ignore silently methods that have a
				// set method and no get, and the inverse
			}
		}

		if (retAdapters.isEmpty()) {
			throw new KasperQueryAdapterException(
					"No property has been discovered for query "
							+ typeToken.getRawType());
		}

		return new BeanQueryMapper(creator, retAdapters);
	}

	private PropertyAdapter createPropertyAdapter(Method mutator,
			Method accessor, String name, TypeToken<Object> propertyType) {
		final ITypeAdapter<Object> delegateAdapter = create(propertyType);

		if (null != delegateAdapter) {
			final PropertyAdapter propertyAdapter = new PropertyAdapter(
					propertyType, accessor, mutator, name, delegateAdapter);
			return propertyAdapter;
		} else {
			throw new KasperQueryAdapterException(
					"Complex Queries are not supported! "
							+ "Please flatten your Pojo in order to contain only java literal "
							+ "properties or register custom a TypeAdapter.");
		}
	}

	private BeanConstructor resolveBeanConstructor(Class<?> forClass) {
		final Constructor<?>[] ctrs = forClass.getDeclaredConstructors();

		// we want the no arg ctr on top
		Arrays.sort(ctrs, LEAST_PARAM_COUNT_CTR_COMPARATOR);

		// we choose the first one, because it is crazy to try to guess which is
		// better to use
		// so the rule is choose the ctr with the less number of params
		@SuppressWarnings("unchecked")
		final Constructor<Object> ctr = (Constructor<Object>) ctrs[0];

		// we now must resolve the parameter names
		final String[] names = paranamer.lookupParameterNames(ctr);
		if (names.length != ctr.getParameterTypes().length)
			throw new KasperQueryAdapterException(
					"Could not resolve constructor[" + ctr
							+ "] parameter names");
		Map<String, BeanConstructorProperty> parameters = new HashMap<String, BeanConstructorProperty>();
		for (int i = 0; i < names.length; i++) {
			parameters.put(
					names[i],
					new BeanConstructorProperty(i, ctr
							.getParameterAnnotations()[i], names[i], ctr
							.getGenericParameterTypes()[i]));
		}

		return new BeanConstructor(ctr, parameters);
	}

	private void collectAccessors(Class<?> fromClass,
			Map<String, Method> accessors) {
		final Method[] methods = fromClass.getDeclaredMethods();

		for (final Method m : methods) {
			if (visibilityFilter.isVisible(m) && isAccessor(m)) {
				String name = resolveName(m);
				if (!accessors.containsKey(name)) {
					accessors.put(name, m);
				}
			}
		}
	}

	private void collectMutators(Class<?> fromClass,
			Map<String, Method> mutators) {
		final Method[] methods = fromClass.getDeclaredMethods();

		for (final Method m : methods) {
			if (visibilityFilter.isVisible(m) && isMutator(m)) {
				String name = resolveName(m);
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
			return firstCharToLowerCase(methodName
					.substring(PREFIX_METHOD_IS_LEN));
		}

		if (methodName.startsWith(PREFIX_METHOD_GET)) {
			return firstCharToLowerCase(methodName
					.substring(PREFIX_METHOD_GET_LEN));
		}

		if (methodName.startsWith(PREFIX_METHOD_SET)) {
			return firstCharToLowerCase(methodName
					.substring(PREFIX_METHOD_SET_LEN));
		}

		throw new IllegalStateException(
				"Method must respect Java Bean conventions and start with is, get or set.");
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

	private boolean isMutator(final Method method) {

		if (method.getName().startsWith(PREFIX_METHOD_SET)
				&& (method.getName().length() > PREFIX_METHOD_SET_LEN)
				&& method.getParameterTypes().length == 1) {
			return true;
		}

		return false;
	}

	// ------------------------------------------------------------------------

	private class BeanQueryMapper implements ITypeAdapter<IQuery> {
		private final Set<PropertyAdapter> adapters;
		private final BeanConstructor queryCtr;

		public BeanQueryMapper(final BeanConstructor queryCtr,
				final Set<PropertyAdapter> adapters) {
			this.adapters = ImmutableSet.copyOf(adapters);
			this.queryCtr = queryCtr;
		}

		@Override
		public void adapt(final IQuery value, final QueryBuilder builder) {
			for (final PropertyAdapter adapter : adapters) {
				adapter.adapt(value, builder);
			}
		}

		@Override
		public IQuery adapt(QueryParser parser) {
			Object[] ctrParams = new Object[queryCtr.parameters.size()];
			List<Pair<PropertyAdapter, Object>> valuesToSet = new ArrayList<Pair<PropertyAdapter, Object>>();
			for (final PropertyAdapter adapter : adapters) {
				Object value = adapter.adapt(parser);
				BeanConstructorProperty ctrParam = queryCtr.parameters
						.get(adapter.getName());
				if (ctrParam != null) {
					ctrParams[ctrParam.position] = value;
				} else
					valuesToSet.add(new Pair<PropertyAdapter, Object>(adapter,
							value));
			}

			Object queryInstance = queryCtr.create(ctrParams);
			for (Pair<PropertyAdapter, Object> pair : valuesToSet)
				pair.firstValue.mutate(queryInstance, pair.secondValue);

			return (IQuery) queryInstance;
		}
	}

	private class Pair<F, S> {
		private F firstValue;
		private S secondValue;

		public Pair(F firstValue, S secondValue) {
			this.firstValue = firstValue;
			this.secondValue = secondValue;
		}
	}

	private class BeanConstructor {
		private final Constructor<Object> ctr;
		private final Map<String, BeanConstructorProperty> parameters;

		public BeanConstructor(final Constructor<Object> ctr,
				final Map<String, BeanConstructorProperty> parameters) {
			this.ctr = ctr;
			this.parameters = parameters;
		}

		public Object create(Object[] params) {
			try {
				return ctr.newInstance(params);
				// OMG...
			} catch (IllegalArgumentException e) {
				throw couldNotInstanciateQuery(e);
			} catch (InstantiationException e) {
				throw couldNotInstanciateQuery(e);
			} catch (IllegalAccessException e) {
				throw couldNotInstanciateQuery(e);
			} catch (InvocationTargetException e) {
				throw couldNotInstanciateQuery(e);
			}
		}

		private KasperQueryAdapterException couldNotInstanciateQuery(Exception e) {
			return new KasperQueryAdapterException(
					"Failed to instanciate query of type "
							+ ctr.getDeclaringClass(), e);
		}
	}

	private class BeanConstructorProperty {
		private final int position;
		@SuppressWarnings("unused")
		private final Annotation[] annotations;
		private final String name;
		private final Type type;

		public BeanConstructorProperty(int position, Annotation[] annotations,
				String name, Type type) {
			this.position = position;
			this.annotations = annotations;
			this.name = name;
			this.type = type;
		}
	}
}
