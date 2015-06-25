// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.facet;

import com.viadeo.kasper.event.saga.step.Step;

import java.lang.reflect.Method;

public interface FacetApplier {
    Step apply(Method method, Step step);
    int getPhase();
}
