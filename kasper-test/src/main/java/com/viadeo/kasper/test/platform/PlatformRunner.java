// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * The <code>PlatformRunner</code> is a custom extension of {@link BlockJUnit4ClassRunner} which allows to mount a
 * platform.
 **/
public class PlatformRunner extends BlockJUnit4ClassRunner {

    /**
     * The <code>Bundles</code> annotation specifies a set of bundle that will be use in order to build the platform.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Bundles {
        Class<? extends DomainBundle>[] list() default {};
    }

    /**
     * The <code>Configuration</code> annotation specifies which platform configuration will be load in order to build
     * the platform.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Configuration {
        Class<? extends PlatformConfiguration> value() default KasperPlatformConfiguration.class;
    }

    /**
     * The <code>InfrastructureContext</code> annotation specifies infrastructure components that can be required in
     * order to instantiate bundle.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface InfrastructureContext {
        Class[] configurations() default {};
    }

    // ------------------------------------------------------------------------

    private final AnnotationConfigApplicationContext applicationContext;

    // ------------------------------------------------------------------------

    public PlatformRunner(final Class<?> clazz) throws InitializationError, ReflectiveOperationException {
        super(clazz);

        applicationContext = new AnnotationConfigApplicationContext();

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // Define an application context of infrastructure components
        final ApplicationContext applicationContextOfInfra = createApplicationContextOf(clazz.getAnnotation(InfrastructureContext.class));

        // Create bundle instances
        final List<DomainBundle> domainBundles = createDomainBundle(applicationContextOfInfra, clazz.getAnnotation(Bundles.class));

        // Initialize the builder
        final Platform.Builder platformBuilder = initializeBuilder(clazz.getAnnotation(Configuration.class));

        // Specialize the builder with the bundles
        for (final DomainBundle domainBundle : domainBundles) {
            platformBuilder.addDomainBundle(domainBundle);

            beanFactory.registerSingleton(
                    domainBundle.getName().trim().isEmpty() ?
                            domainBundle.getClass().getSimpleName()
                          : domainBundle.getName(),
                    domainBundle
            );
        }

        // Build the platform
        final Platform platform = platformBuilder.build();

        beanFactory.registerSingleton("platform", platform);
        beanFactory.registerSingleton("commandGateway", platform.getCommandGateway());
        beanFactory.registerSingleton("queryGateway", platform.getQueryGateway());
        beanFactory.registerSingleton("eventBus", platform.getEventBus());

        applicationContext.refresh();
    }

    protected Platform.Builder initializeBuilder(final Configuration configuration) throws ReflectiveOperationException {
        final PlatformConfiguration platformConfiguration;

        if (null == configuration) {
            platformConfiguration = new KasperPlatformConfiguration();
        } else {
            platformConfiguration = configuration.value().newInstance();
        }

        return new Platform.Builder(platformConfiguration);
    }

    protected List<DomainBundle> createDomainBundle(final ApplicationContext applicationContext,
                                                    final Bundles bundlesAnnotation)
            throws ReflectiveOperationException {
        final List<DomainBundle> domainBundles = Lists.newArrayList();

        if (null != bundlesAnnotation) {
            for (final Class domainBundleClass : bundlesAnnotation.list()) {
                final Constructor constructor = domainBundleClass.getDeclaredConstructors()[0];

                final List<Object> parameters = Lists.newArrayList();
                for (final Class parameterClass : constructor.getParameterTypes()) {
                    parameters.add(applicationContext.getBean(parameterClass));
                }

                final Object[] initargs = parameters.toArray(new Object[parameters.size()]);
                final DomainBundle domainBundle = (DomainBundle) constructor.newInstance(initargs);
                domainBundles.add(domainBundle);
            }
        }

        return domainBundles;
    }

    protected ApplicationContext createApplicationContextOf(InfrastructureContext infrastructureContextAnnotation) {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        if (null != infrastructureContextAnnotation) {
            applicationContext.register(infrastructureContextAnnotation.configurations());
        }
        applicationContext.refresh();
        return applicationContext;
    }

    @Override
    protected Object createTest() throws Exception {
        final Object bean = super.createTest();

        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        beanFactory.initializeBean(bean, bean.getClass().getName());

        return bean;
    }

}

