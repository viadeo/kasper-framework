// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemorySagaRepository implements SagaRepository {

    private final Map<Object, Map<String,Object>> sagas;
    private final SagaMapper sagaMapper;

    public InMemorySagaRepository(SagaFactory sagaFactory) {
        this.sagaMapper = new SagaMapper(sagaFactory);
        this.sagas = Maps.newHashMap();
    }

    @Override
    public Optional<Saga> load(Object identifier) {
        checkNotNull(identifier);

        Map<String,Object> properties = sagas.get(identifier);

        if( properties != null ) {
            return Optional.fromNullable(sagaMapper.to(properties));
        }

        return Optional.absent();
    }

    @Override
    public void save(Object identifier, Saga saga) {
        checkNotNull(identifier);
        checkNotNull(saga);

        Map<String, Object> properties = sagaMapper.from(identifier, saga);

        sagas.put(identifier, properties);
    }

    @Override
    public void delete(Object identifier) {
        sagas.remove(identifier);
    }
}
