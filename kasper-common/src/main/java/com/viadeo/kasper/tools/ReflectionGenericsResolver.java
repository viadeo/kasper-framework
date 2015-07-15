// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * Utility class used to retrieve types of parameterized classes
 * @author Mglcel &lt;ldiasdasilva@viadeoteam.com&gt;
 *
 */
public final class ReflectionGenericsResolver {

	private ReflectionGenericsResolver() { /* singleton */ }
	
	/**
	 * @param runtimeType the runtime class to be analyzed
	 * @param targetType the target type to resolve the runtimeType against
	 * @param nbParameter the generic parameter position on the targetType
	 * 
	 * @return the (optional) type of the resolved parameter at specific position
	 * 
	 * ex:
	 * targetClass implements targetType&lt;Integer, String&gt;
	 * getParameterTypeFromClass(targetClass, targetType, 1) ==&gt; String
	 *
	 * ex:
	 * targetClass extends temporary&lt;Integer, String&gt;
	 * temporary&lt;R, B&gt; implements targetType&lt;A,G&gt;
	 * getParameterTypeFromClass(targetClass, targetType, 0) ==&gt; Integer
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static Optional<? extends Class> getParameterTypeFromClass(final Type runtimeType,
                                                                      final Type targetType,
			                                                          final Integer nbParameter) {
		// Boot recursive process with an empty bindings maps
		return getParameterTypeFromClass(
                runtimeType,
                targetType,
                nbParameter,
                new HashMap<Type, Type>()
        );
	}

    /**
     * Can be used to analyze a field, taking into account the generic parameters of its declaring class
     *
     * @param runtimeField the runtime field to be analyzed
	 * @param targetType the target type to resolve the runtimeType against
	 * @param nbParameter the generic parameter position on the targetType
	 *
	 * @return the (optional) type of the resolved parameter at specific position
     */
 	@SuppressWarnings("rawtypes")
	public static Optional<? extends Class> getParameterTypeFromClass(final Field runtimeField,
                                                                      final Type targetType,
			                                                          final Integer nbParameter) {
        final Map<Type, Type> bindings = Maps.newHashMap();
        fillBindingsFromClass(runtimeField.getDeclaringClass(), bindings);

		// Boot recursive process with an empty bindings maps
		return getParameterTypeFromClass(
                runtimeField.getGenericType(),
                targetType,
                nbParameter,
                bindings
        );
	}

     /**
     * Can be used to analyze a field, taking into account the generic parameters of the specified declaring type
     *
     * @param runtimeField the runtime field to be analyzed
     * @param declaringType the declaring type to take into account for generic parameters analysis
	 * @param targetType the target type to resolve the runtimeType against
	 * @param nbParameter the generic parameter position on the targetType
	 *
	 * @return the (optional) type of the resolved parameter at specific position
     */
 	@SuppressWarnings("rawtypes")
	public static Optional<? extends Class> getParameterTypeFromClass(final Field runtimeField,
                                                                      final Type declaringType,
                                                                      final Type targetType,
			                                                          final Integer nbParameter) {
        final Map<Type, Type> bindings = Maps.newHashMap();
        fillBindingsFromClass(declaringType, bindings);

        Optional<Class> clazz = getClass(declaringType);
        while (clazz.isPresent()) {
            final Type parent = clazz.get().getGenericSuperclass();
            fillBindingsFromClass(parent, bindings);
            clazz = getClass(parent);
        }

		// Boot recursive process with an empty bindings maps
		return getParameterTypeFromClass(
                runtimeField.getGenericType(),
                targetType,
                nbParameter,
                bindings
        );
	}

	// ========================================================================

	/**
	 * Get class from type, taking care of parameterized types
	 *
	 * @param type a type or class to be resolved
	 * 
	 * @return the (optional) class
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static Optional<Class> getClass(final Type type) {
		final Optional<Class> ret;

		if (type instanceof Class) {
			ret = Optional.of((Class) type);
		} else if (type instanceof ParameterizedType) {
			ret = getClass(((ParameterizedType) type).getRawType());
		} else {
			ret = Optional.absent();
		}

		return ret;
	}

	// ------------------------------------------------------------------------

	/**
	 * Resolve generic parameters bindings during class hierarchy traversal
	 * 
	 * @param classType the class to be analyzed
	 * @param bindings the bindings map to fill
	 * 
	 */
	private static void fillBindingsFromClass(final Type classType, final Map<Type, Type> bindings) {
		if (classType instanceof ParameterizedType) {

			final Type[] paramTypes = ((ParameterizedType) classType).getActualTypeArguments();
			final Class rawClass = (Class) ((ParameterizedType) classType).getRawType();
			final Type[] rawTypes = rawClass.getTypeParameters();

			int i = 0;
			for (final Type rawType : rawTypes) {
				if ( ! getClass(rawType).isPresent()) {
					Type bindType = paramTypes[i];
					if ( ! getClass(bindType).isPresent()) {
						bindType = bindings.get(bindType);
					}
					bindings.put(rawType, bindType);
				}
				i++;
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Algorithm body - recurse on class hierarchy taking care of type bindings
	 * 
	 * @param runtimeType the runtime class to be analyzed
	 * @param targetType the target type to resolve the runtimeType against
	 * @param nbParameter the generic parameter position on the targetType
	 * @param bindings the current type binding map
	 * 
	 * @return the (optional) resolved type
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Optional<Class> getParameterTypeFromClass(final Type runtimeType,
                                                             final Type targetType,
			                                                 final Integer nbParameter,
                                                             final Map<Type, Type> bindings) {

		final Optional<Class> runtimeClass = getClass(runtimeType);
		final Optional<Class> targetClass = getClass(targetType);

		if ( ! runtimeClass.isPresent() || !targetClass.isPresent()) {
			return Optional.absent();
		}

		if ( ! targetClass.get().isAssignableFrom(runtimeClass.get())) {
			return Optional.absent();
		}

		// First step : directly accessible information ---------------------------
		fillBindingsFromClass(runtimeType, bindings);

		final Type[] types = runtimeClass.get().getGenericInterfaces();

		final List<Type> currentTypes = new ArrayList<Type>();
		currentTypes.add(runtimeType);
		currentTypes.addAll(Arrays.asList(types));

		for (final Type type : currentTypes) {
			if (getClass(type).equals(targetClass) && ParameterizedType.class.isAssignableFrom(type.getClass())) {

				final ParameterizedType pt = (ParameterizedType) type;
				final Type[] parameters = pt.getActualTypeArguments();
				final Type parameter = parameters[nbParameter];

				Optional<Class> retClass = getClass(parameter);
				if ( ! retClass.isPresent()) {
					retClass = getClass(bindings.get(parameter));
				}

				return retClass;
			}
		}

		// Second step : parent and implemented interfaces ------------------------
		final Type parent = runtimeClass.get().getGenericSuperclass();
		final Type[] interfaces = runtimeClass.get().getGenericInterfaces();

		final List<Type> proposalTypes = new ArrayList<Type>();
		proposalTypes.add(parent);
		proposalTypes.addAll(Arrays.asList(interfaces));

		for (final Type proposalType : proposalTypes) {
			if (null != proposalType) {
				fillBindingsFromClass(proposalType, bindings);

				final Optional<Class> retClass = getParameterTypeFromClass(
                        proposalType, targetType,
                        nbParameter, bindings
                );

				if (retClass.isPresent()) {
					return retClass;
				}
			}
		}

		return Optional.absent();
	}

}
