// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step;

import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.Saga;

import java.util.Set;

/**
 * Interface describing the control of saga's steps
 */
public interface StepChecker {

    void check(Class<? extends Saga> sagaClass, Set<Step> steps);

}
