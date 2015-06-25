// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.repository;

import com.google.common.collect.Maps;
import com.viadeo.kasper.event.saga.SagaFactory;
import com.viadeo.kasper.event.saga.SagaMapper;

import java.util.Map;

public class InMemorySagaRepository extends BaseSagaRepository {

    private final Map<Object, Map<String,Object>> sagas;

    // ------------------------------------------------------------------------

    public InMemorySagaRepository(final SagaFactory sagaFactory) {
        super(new SagaMapper(sagaFactory));
        this.sagas = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    @Override
    public Map<String, Object> doLoad(final Object identifier) {
        return sagas.get(identifier);
    }

    @Override
    public void doSave(final Object identifier, final Map<String, Object> sagaProperties) {
        sagas.put(identifier, sagaProperties);
    }

    @Override
    public void delete(final Object identifier) {
        sagas.remove(identifier);
    }

}
