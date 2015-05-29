// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.saga.AssociationValue;
import org.axonframework.saga.Saga;
import org.axonframework.saga.repository.jpa.JpaSagaRepository;

import java.util.Set;

public class KasperSagaRepository implements org.axonframework.saga.SagaRepository {

    @Override
    public Set<String> find(Class<? extends Saga> type, AssociationValue associationValue) {
        return null;
    }

    @Override
    public Saga load(String sagaIdentifier) {
        return null;
    }

    @Override
    public void commit(Saga saga) {

    }

    @Override
    public void add(Saga saga) {

    }
}
