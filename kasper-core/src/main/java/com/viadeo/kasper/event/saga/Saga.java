// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.factory.SagaFactory;

/**
 * Interface describing an implementation of a Saga. Sagas are instances that handle events and may possibly produce
 * new commands or have other side effects. Typically, Sagas are used to manage long running business transactions.
 */
public interface Saga {

    Optional<SagaFactory> getFactory();

    Optional<SagaIdReconciler> getIdReconciler();

}
