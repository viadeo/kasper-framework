// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step.facet;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.step.Step;

public abstract class DecorateStep implements Step {

    private final Step delegateStep;

    public DecorateStep(Step delegateStep) {
        this.delegateStep = Preconditions.checkNotNull(delegateStep);
    }

    @Override
    public String name() {
        return delegateStep.name();
    }

    @Override
    public Class<? extends Event> getSupportedEvent() {
        return delegateStep.getSupportedEvent();
    }

    @Override
    public <T> Optional<T> getSagaIdentifierFrom(Event event) {
        return delegateStep.getSagaIdentifierFrom(event);
    }

    @Override
    public Class<? extends Saga> getSagaClass() {
        return delegateStep.getSagaClass();
    }


    public Step getDelegateStep() {
        return delegateStep;
    }
}
