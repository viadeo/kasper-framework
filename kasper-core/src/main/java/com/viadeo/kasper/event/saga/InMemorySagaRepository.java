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

    private final Map<Object, Saga> sagas;

    public InMemorySagaRepository() {
        this.sagas = Maps.newHashMap();
    }

    @Override
    public Optional<Saga> load(Object identifier) {
        checkNotNull(identifier);
        return Optional.fromNullable(sagas.get(identifier));
    }

    @Override
    public void save(Object identifier, Saga saga) {
        checkNotNull(identifier);
        checkNotNull(saga);
        sagas.put(identifier, saga);
    }

    @Override
    public void delete(Object identifier) {
        sagas.remove(identifier);
    }
}
