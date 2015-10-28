// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.factory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.saga.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultSagaFactoryProvider implements SagaFactoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSagaFactoryProvider.class);

    private static final Map<Class<? extends Saga>, SagaFactory> CACHE = Maps.newHashMap();

    @VisibleForTesting
    public static void clearCache() {
        CACHE.clear();
    }

    @Override
    public <SAGA extends Saga> Optional<SagaFactory> get(final Class<SAGA> sagaClass) {
        return Optional.fromNullable(CACHE.get(sagaClass));
    }

    @Override
    public SagaFactory getOrCreate(final Saga saga) {
        final Optional<SagaFactory> optionalSagaFactory = get(saga.getClass());

        if (optionalSagaFactory.isPresent()) {
            return optionalSagaFactory.get();
        }

        final SagaFactory sagaFactory = new DefaultSagaFactory();
        CACHE.put(saga.getClass(), sagaFactory);

        return sagaFactory;
    }
}
