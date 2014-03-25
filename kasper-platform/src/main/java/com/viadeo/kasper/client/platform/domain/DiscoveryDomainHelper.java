// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DiscoveryDomainHelper {

    @SuppressWarnings("unchecked")
    public static <COMP> COMP instantiateComponent(final ApplicationContext applicationContext,
                                                   final Class<COMP> componentClass)
            throws ReflectiveOperationException {

        final Constructor constructor = componentClass.getDeclaredConstructors()[0];

        final List<Object> parameters = Lists.newArrayList();
        for (final Class parameterClass : constructor.getParameterTypes()) {
            parameters.add(applicationContext.getBean(parameterClass));
        }

        final Object[] initargs = parameters.toArray(new Object[parameters.size()]);
        return (COMP) constructor.newInstance(initargs);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <COMP> Collection<Class<COMP>> findComponents(final String basePackage,
                                                                final Class<COMP> componentClazz)
            throws ReflectiveOperationException {

        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(componentClazz));

        final Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(basePackage);

        final List<Class<COMP>> componentClasses = Lists.newArrayList();
        for (final BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            componentClasses.add((Class<COMP>) clazz);
        }

        return componentClasses;
    }

    // ------------------------------------------------------------------------

    public static Domain findCandidateDomain(final String basePackage) throws KasperException {
        try {

            final Collection<Class<Domain>> candidateDomains = findComponents(basePackage, Domain.class);

            if ( (null == candidateDomains) || (0 == candidateDomains.size()) ) {
                throw new IllegalStateException("No found domain with `" + basePackage + "` as base package");
            }

            if (candidateDomains.size() > 1) {
                throw new IllegalStateException("Too many domain found with `" + basePackage + "` : " + candidateDomains);
            }

            final Class<Domain> domainClass = candidateDomains.iterator().next();
            final Constructor<?>[] constructors = domainClass.getDeclaredConstructors();

            return (Domain) constructors[0].newInstance();

        } catch (final ReflectiveOperationException e) {
            throw new KasperException(e);
        }
    }

}
