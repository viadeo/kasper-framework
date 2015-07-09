// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.spring.config.KasperSpringConfigPropertySource;
import com.viadeo.kasper.spring.config.KasperSpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.List;

public class KasperSpringContextBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperSpringContextBuilder.class);

    private Config config;
    private List<Class<?>> bundles;
    private Class<?>[] parent;

    public KasperSpringContextBuilder() {
        this.bundles = Lists.newArrayList();
    }

    /**
     * Transform a FQN string to a class instance
     *
     * @param fqnList a list of fqn
     * @return a list of class instances
     */
    private static List<Class<?>> transformToClasses(List<String> fqnList) {
        if (fqnList == null || fqnList.isEmpty()) {
            return ImmutableList.of();
        }
        ArrayList<Class<?>> objects = Lists.newArrayList();
        for (String className : fqnList) {
            try {
                objects.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new KasperException("Unable to found ", e);
            }
        }

        return objects;
    }

    /**
     * A list of bundleConfigs to run
     *
     * @param bundle bundle
     * @return platform builder
     */
    public KasperSpringContextBuilder addBundle(Class<? extends DomainBundle> bundle) {
        this.bundles.add(bundle);
        return this;
    }

    /**
     * Type safe configuration, used in conjunction with
     * spring factory beans to perform wiring
     *
     * @param config type safe config
     * @return platform builder
     */
    public KasperSpringContextBuilder withConfig(Config config) {
        this.config = config;
        return this;
    }

    /**
     * Setup parent configurations
     *
     * @param configurations a list of spring factory beans
     * @return platform builder
     */
    public KasperSpringContextBuilder withParent(Class<?>... configurations) {
        this.parent = configurations;
        return this;
    }


    /**
     * Main entry point for the platform
     * <p/>
     * Setup two context :
     * - parent contains infrastructure and glue code
     * - bundles contains isolated contexts inheriting from parent
     * <p/>
     * Domains are
     */
    public AnnotationConfigApplicationContext build() {

        try {
            if (null == this.config) {
                this.config = KasperSpringConfiguration.configuration();
            }

            if (this.bundles.isEmpty()) {
                this.bundles = transformToClasses(config.getStringList("runtime.spring.domains"));
            }

            if (null == this.parent) {
                this.parent = new Class<?>[]{};
            }

            AnnotationConfigApplicationContext context = contextWithConfig(config);
            context.register(parent);
            context.register(bundles.toArray(new Class<?>[bundles.size()]));
            context.registerShutdownHook();
            context.refresh();

            return context;
        } catch (Exception e) {
            LOGGER.error("failed to start application", e);
            throw Throwables.propagate(e);
        }
    }

    /**
     * Prepare a context with an active profile matching the configuration
     * And register the configuration into bean factory
     *
     * @param config type safe config
     * @return context ready to go
     */
    private AnnotationConfigApplicationContext contextWithConfig(Config config) {

        AnnotationConfigApplicationContext platform = new AnnotationConfigApplicationContext();
        ConfigurableEnvironment environment = platform.getEnvironment();
        environment.getPropertySources().addLast(new KasperSpringConfigPropertySource(config, "config"));

        try {
            for (String activeProfile : config.getStringList("runtime.spring.profiles.actives")) {
                environment.addActiveProfile(activeProfile);
            }
        } catch (ConfigException.Missing | ConfigException.WrongType e ) {
            LOGGER.error("Failed to load property 'runtime.spring.profiles.actives'", e);
        }

        LOGGER.info("active profiles are : {}", Joiner.on(", ").join(environment.getActiveProfiles()));

        ConfigurableListableBeanFactory beanFactory = platform.getBeanFactory();
        beanFactory.registerSingleton("configuration", config);
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setEnvironment(environment);
        beanFactory.registerSingleton("propertySourcesPlaceholderConfigurer", placeholderConfigurer);

        return platform;
    }
}
