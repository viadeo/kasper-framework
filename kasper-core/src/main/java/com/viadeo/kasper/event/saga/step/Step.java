// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.Saga;

public interface Step {
    String name();
    void invoke(Saga saga, Event event) throws StepInvocationException;
    Class<? extends Event> getSupportedEvent();
    <T> Optional<T> getSagaIdentifierFrom(Event event);
}
