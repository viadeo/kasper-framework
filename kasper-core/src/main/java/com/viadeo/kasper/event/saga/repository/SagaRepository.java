// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.Saga;

/**
 * Interface to implement in order to store sagas instances.
 */
public interface SagaRepository {

    Optional<Saga> load(Object identifier);

    void save(Object identifier, Saga saga);

    void delete(Object identifier);

}
