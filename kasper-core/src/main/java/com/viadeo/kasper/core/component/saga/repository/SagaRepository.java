// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.exception.SagaPersistenceException;

/**
 * Interface to implement in order to store sagas instances.
 */
public interface SagaRepository {

    Optional<Saga> load(Object identifier) throws SagaPersistenceException;

    void save(Object identifier, Saga saga) throws SagaPersistenceException;

    void delete(Object identifier) throws SagaPersistenceException;

}