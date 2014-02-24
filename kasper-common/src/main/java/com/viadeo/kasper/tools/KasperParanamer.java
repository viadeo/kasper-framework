// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.module.paranamer.shaded.BytecodeReadingParanamer;
import com.fasterxml.jackson.module.paranamer.shaded.DefaultParanamer;
import com.fasterxml.jackson.module.paranamer.shaded.ParameterNamesNotFoundException;
import com.fasterxml.jackson.module.paranamer.shaded.Paranamer;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This implementation of Paranamer allows to fix bad behavior encounter when we have several constructors of an
 * immutable class.
 */
public class KasperParanamer implements Paranamer {

    private final Paranamer delegate;
    private final Paranamer fallback;

    public KasperParanamer() {
        this(new BytecodeReadingParanamer(), new DefaultParanamer());
    }

    public KasperParanamer(final Paranamer delegate, final Paranamer fallback) {
        this.delegate = checkNotNull(delegate);
        this.fallback = checkNotNull(fallback);
    }

    protected boolean isImmutable(final Class declaringClass) {
        try {
            final List<Method> writeMethods = Lists.newArrayList();
            final BeanInfo beanInfo = Introspector.getBeanInfo(declaringClass);

            for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                final Method method = propertyDescriptor.getWriteMethod();
                if (null != method) {
                    writeMethods.add(method);
                }
            }

            return writeMethods.isEmpty();

        } catch (IntrospectionException e) {
            return false;
        }
    }

    @Override
    public String[] lookupParameterNames(final AccessibleObject accessibleObject) {
        return lookupParameterNames(accessibleObject, true);
    }

    @Override
    public String[] lookupParameterNames(
            final AccessibleObject accessibleObject,
            final boolean throwExceptionIfMissing
    ) {

        if (!Constructor.class.isAssignableFrom(accessibleObject.getClass())) {
            return EMPTY_NAMES;
        }

        final Constructor constructor = (Constructor) accessibleObject;
        final Class declaringClass = constructor.getDeclaringClass();

        if (!isImmutable(declaringClass)) {
            return delegate.lookupParameterNames(accessibleObject, throwExceptionIfMissing);
        }

        checkClassDefinition(declaringClass);

        if (declaringClass.getDeclaredConstructors().length <= 1) {
            try {
                return delegate.lookupParameterNames(accessibleObject, true);
            } catch (ParameterNamesNotFoundException e) {
                return fallback.lookupParameterNames(accessibleObject, true);
            }
        } else if (null == constructor.getAnnotation(JsonCreator.class)) {
            return EMPTY_NAMES;
        }

        return extractParameterNames(constructor);
    }

    /**
     * Extract parameter names from JsonProperty annotation.
     *
     * @param constructor the constructor
     * @return the list of parameter names
     */
    protected String[] extractParameterNames(final Constructor constructor) {
        checkNotNull(constructor);
        final Class[] parameterTypes = constructor.getParameterTypes();
        final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

        final String[] parameterNames = new String[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Optional<JsonProperty> optionalAnnotation = Optional.absent();

            for (Annotation annotation : parameterAnnotations[i]) {
                if (JsonProperty.class.isInstance(annotation)) {
                    optionalAnnotation = Optional.of((JsonProperty) annotation);
                    break;
                }
            }

            if (optionalAnnotation.isPresent()) {
                parameterNames[i] = optionalAnnotation.get().value();
            } else {
                final String message = String.format(
                        "One or more @JsonProperty annotations are missing on the constructor with this parameters '%s' of the class '%s'",
                        Lists.newArrayList(parameterTypes),
                        constructor.getDeclaringClass()
                );

                throw new ParameterNamesNotFoundException(message);
            }
        }

        return parameterNames;
    }

    /**
     * Check that the specified object has only one constructor or only one annotated constructor with JsonCreator
     * among several
     *
     * @param declaringClass the declaring class of the object
     */
    protected void checkClassDefinition(final Class<?> declaringClass) {
        final Constructor[] declaredConstructors = declaringClass.getDeclaredConstructors();

        if (declaredConstructors.length <= 1) {
            return;
        }

        Constructor annotatedConstructor = null;
        for (Constructor constructor : declaredConstructors) {
            if (null != constructor.getAnnotation(JsonCreator.class)) {
                if (null != annotatedConstructor) {
                    final String message = String.format(
                            "Only one constructor should be annotated with @JsonCreator annotation for the class '%s'",
                            declaringClass
                    );
                    throw new RuntimeException(message);
                }

                annotatedConstructor = constructor;
            }
        }
    }
}
