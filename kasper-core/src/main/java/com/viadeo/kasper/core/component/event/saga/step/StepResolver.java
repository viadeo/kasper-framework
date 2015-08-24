// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;

import java.util.Set;

/**
 * Interface describing the extraction of Steps present in a Saga
 */
public interface StepResolver {

    /**
     * Extract different <code>Step</code> from the <code>Saga</code>
     *
     * @param sagaClass the saga class
     * @param idReconciler the id reconciler
     * @return a set of <code>Step</code> defining by the saga class
     */
    Set<Step> resolve(Class<? extends Saga> sagaClass, SagaIdReconciler idReconciler);

}
