// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.Set;

/**
 * Interface describing the control of saga's steps
 */
public interface StepChecker {

    /**
     * check if the <code>Step</code> are used correctly for the given <code>Saga</code>
     *
     * @param sagaClass the saga class
     * @param steps a list of <code>Step</code> describing this saga
     */
    void check(Class<? extends Saga> sagaClass, Set<Step> steps);

}
