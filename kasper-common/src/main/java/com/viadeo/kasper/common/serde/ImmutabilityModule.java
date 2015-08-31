// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import com.fasterxml.jackson.module.paranamer.shaded.*;
import com.google.common.collect.Lists;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ImmutabilityModule extends ParanamerModule {

    private static final long serialVersionUID = 2291876538816364449L;

    public ImmutabilityModule() {
        super(new CachingParanamer(new KasperParanamer()));
    }

    // ------------------------------------------------------------------------

    /**
     * This implementation of Paranamer allows to fix bad behavior encounter when we have several constructors of an
     * immutable class.
     */
    public static class KasperParanamer implements Paranamer {

        private final Paranamer delegate;
        private final Paranamer fallback;

        public KasperParanamer() {
            this(new BytecodeReadingParanamer(), new DefaultParanamer());
        }

        public KasperParanamer(final Paranamer delegate, final Paranamer fallback) {
            this.delegate = checkNotNull(delegate);
            this.fallback = checkNotNull(fallback);
        }

        public boolean isImmutable(final Class declaringClass) {
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
                final boolean throwExceptionIfMissing)
        {

            if ( ! Constructor.class.isAssignableFrom(accessibleObject.getClass())) {
                return EMPTY_NAMES;
            }

            final Constructor constructor = (Constructor) accessibleObject;
            final Class declaringClass = constructor.getDeclaringClass();

            if ( ! isImmutable(declaringClass)) {
                return delegate.lookupParameterNames(accessibleObject, throwExceptionIfMissing);
            }

            checkClassDefinition(declaringClass);

            if (declaringClass.getDeclaredConstructors().length <= 1) {
                try {
                    return delegate.lookupParameterNames(accessibleObject, true);
                } catch (final ParameterNamesNotFoundException e) {
                    return fallback.lookupParameterNames(accessibleObject, true);
                }
            }

            return EMPTY_NAMES;
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
            for (final Constructor constructor : declaredConstructors) {
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

    // ------------------------------------------------------------------------
}
