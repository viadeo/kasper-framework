// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.factory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultSagaFactoryProvider implements SagaFactoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSagaFactoryProvider.class);

    private static final Map<Class<? extends Saga>, SagaFactory> CACHE = Maps.newHashMap();

    @VisibleForTesting
    public static void clearCache() {
        CACHE.clear();
    }

    private final ApplicationContext applicationContext;

    public DefaultSagaFactoryProvider(final ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
    }

    @Override
    public Optional<SagaFactory> get(final Class<? extends Saga> sagaClass) {
        return Optional.fromNullable(CACHE.get(sagaClass));
    }

    @Override
    public SagaFactory getOrCreate(final Saga saga) {
        Optional<SagaFactory> optionalSagaFactory = get(saga.getClass());

        if (optionalSagaFactory.isPresent()) {
            return optionalSagaFactory.get();
        }

        final Set<Class<?>> parameterTypes = Sets.newHashSet();

        for (final Constructor<?> constructor : saga.getClass().getConstructors()) {
            parameterTypes.addAll(Arrays.asList(constructor.getParameterTypes()));
        }

        final GenericApplicationContext sagaContext = new GenericApplicationContext();
        sagaContext.setParent(applicationContext);

        final ConfigurableListableBeanFactory beanFactory = sagaContext.getBeanFactory();

        for (final Field field : saga.getClass().getDeclaredFields()) {
            if (parameterTypes.contains(field.getType()) && applicationContext.getBeanNamesForType(field.getType()).length == 0) {
                field.setAccessible(Boolean.TRUE);
                try {
                    beanFactory.registerSingleton(field.getName(), field.get(saga));
                } catch (IllegalAccessException e) {
                    LOGGER.warn("Failed to enrich the context with '{}'", field.getName(), e);
                }
            }
        }

        sagaContext.refresh();

        SagaFactory sagaFactory = new DefaultSagaFactory(sagaContext);

        CACHE.put(saga.getClass(), sagaFactory);

        return sagaFactory;
    }
}
