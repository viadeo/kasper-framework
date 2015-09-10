// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.base.Optional;

/**
 * Interface describing an implementation of a Saga. Sagas are instances that handle events and may possibly produce
 * new commands or have other side effects. Typically, Sagas are used to manage long running business transactions.
 */
public interface Saga {

    /**
     * get the optional <code>SagaIdReconciler</code> used for ids conversion in order to retrieve a particular <code>Saga</code>
     *
     * @return the Optional <code>SagaIdReconciler</code>
     */
    Optional<SagaIdReconciler> getIdReconciler();

}
