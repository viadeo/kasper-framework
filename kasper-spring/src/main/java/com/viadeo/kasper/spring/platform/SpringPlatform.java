// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.PlatformAware;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.spring.core.ConfigPropertySource;
import com.viadeo.kasper.spring.core.KasperConfiguration;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpringPlatform implements Platform {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final Meta meta;
    private final KasperEventBus eventBus;
    private final ApplicationContext applicationContext;
    private final MetricRegistry metricRegistry;
    private final List<Plugin> platformAwares;

    // ------------------------------------------------------------------------

    private SpringPlatform(final ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
        this.commandGateway = applicationContext.getBean(CommandGateway.class);
        this.queryGateway = applicationContext.getBean(QueryGateway.class);
        this.eventBus = applicationContext.getBean(KasperEventBus.class);
        this.meta = applicationContext.getBean(Meta.class);
        this.metricRegistry = applicationContext.getBean(MetricRegistry.class);
        this.platformAwares = Lists.newArrayList(applicationContext.getBeansOfType(Plugin.class).values());

        Collections.sort(platformAwares, Plugin.REVERSED_COMPARATOR);

        for (final PlatformAware platformAware : platformAwares) {
            if (platformAware instanceof Plugin) {
                platformAware.onPluginRegistered((Plugin) platformAware);
            }
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    @Override
    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    @Override
    public KasperEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Meta getMeta() {
        return meta;
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Override
    public SpringPlatform start() {
        applicationContext.getBean(LifecycleProcessor.class).start();
        firePlatformStarted();
        return this;
    }

    @Override
    public SpringPlatform stop() {
        applicationContext.getBean(LifecycleProcessor.class).stop();
        firePlatformStopped();
        return this;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private void firePlatformStarted() {
        for (final PlatformAware platformAware : platformAwares) {
            platformAware.onPlatformStarted(this);
        }
    }

    private void firePlatformStopped() {
        for (final PlatformAware platformAware : platformAwares) {
            platformAware.onPlatformStopped(this);
        }
    }

    // ========================================================================

    public static class Builder implements Platform.Builder {

        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        private final List<Class<?>> coreConfigurations;
        private final Set<Class<?>> bundles;

        private Config config;

        public Builder() {
            this.coreConfigurations = Lists.newArrayList();
            this.bundles = Sets.newHashSet();
        }

        public Builder withConfig(final Config config) {
            this.config = checkNotNull(config);
            return this;
        }

        public Builder with(final Class<?>... configurations) {
            checkNotNull(configurations);
            coreConfigurations.clear();
            coreConfigurations.addAll(Arrays.asList(configurations));
            return this;
        }

        public Builder add(final Class<?>... configurations) {
            checkNotNull(configurations);
            coreConfigurations.addAll(Arrays.asList(configurations));
            return this;
        }

        public Builder addBundle(final Class<? extends DomainBundle> bundle, final Class<?>... bundles) {
            this.bundles.add(checkNotNull(bundle));
            this.bundles.addAll(Arrays.asList(bundles));
            return this;
        }

        @Override
        public SpringPlatform build() {
            if (null == this.config) {
                this.config = KasperConfiguration.configuration();
            }

            if (this.bundles.isEmpty()) {
                try {
                    this.bundles.addAll(
                            transformToClasses(config.getStringList("runtime.spring.domains"))
                    );
                } catch (ConfigException.Missing e) {
                    LOGGER.error("No domain are declared in configuration");
                }
            }

            AnnotationConfigApplicationContext context = contextWithConfig(config);
            context.register(coreConfigurations.toArray(new Class[coreConfigurations.size()]));
            if (bundles.size() > 0) {
                context.register(bundles.toArray(new Class<?>[bundles.size()]));
            }
            context.registerShutdownHook();
            context.refresh();

            return new SpringPlatform(context);
        }

        /**
         * Prepare a context with an active profile matching the configuration
         * And register the configuration into bean factory
         *
         * @param config type safe config
         * @return context ready to go
         */
        private AnnotationConfigApplicationContext contextWithConfig(final Config config) {

            final AnnotationConfigApplicationContext platform = new AnnotationConfigApplicationContext();
            final ConfigurableEnvironment environment = platform.getEnvironment();
            environment.getPropertySources().addLast(new ConfigPropertySource(config, "config"));

            try {
                if (config.hasPath("runtime.spring.profiles.actives")) {
                    for (final String activeProfile : config.getStringList("runtime.spring.profiles.actives")) {
                        environment.addActiveProfile(activeProfile);
                    }
                }
            } catch (final ConfigException.Missing | ConfigException.WrongType e ) {
                LOGGER.error("Failed to load property 'runtime.spring.profiles.actives'", e);
            }

            LOGGER.info("active profiles are : {}", Joiner.on(", ").join(environment.getActiveProfiles()));

            final ConfigurableListableBeanFactory beanFactory = platform.getBeanFactory();
            beanFactory.registerSingleton("configuration", config);

            final PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
            placeholderConfigurer.setEnvironment(environment);
            beanFactory.registerSingleton("propertySourcesPlaceholderConfigurer", placeholderConfigurer);

            return platform;
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
    }
}
