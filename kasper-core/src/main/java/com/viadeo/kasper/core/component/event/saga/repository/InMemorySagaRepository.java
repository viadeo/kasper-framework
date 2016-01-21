// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.repository;

import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaMapper;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;

import java.util.Map;

/**
 * SagaRepository implementation that stores all Saga instances in memory.
 */
public class InMemorySagaRepository extends BaseSagaRepository {

    private final Map<Object, Map<String,String>> sagas;

    // ------------------------------------------------------------------------

    public InMemorySagaRepository(final SagaFactoryProvider sagaFactoryProvider) {
        super(new SagaMapper(sagaFactoryProvider));
        this.sagas = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    @Override
    public Map<String, String> doLoad(final Class<? extends Saga> sagaClass, final Object identifier) {
        return sagas.get(identifier);
    }

    @Override
    public void doSave(final Class<? extends Saga> sagaClass, final Object identifier, final Map<String, String> sagaProperties) {
        sagas.put(identifier, sagaProperties);
    }

    @Override
    public void delete(final Class<? extends Saga> sagaClass, final Object identifier) {
        sagas.remove(identifier);
    }

}
