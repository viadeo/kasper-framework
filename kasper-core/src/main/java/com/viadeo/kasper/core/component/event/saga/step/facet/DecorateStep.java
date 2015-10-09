// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.step.Step;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class DecorateStep implements Step {

    private final Step delegateStep;

    // ------------------------------------------------------------------------

    public DecorateStep(final Step delegateStep) {
        this.delegateStep = checkNotNull(delegateStep);
    }

    // ------------------------------------------------------------------------

    @Override
    public String name() {
        return delegateStep.name();
    }

    @Override
    public EventDescriptor getSupportedEvent() {
        return delegateStep.getSupportedEvent();
    }

    @Override
    public Optional<Object> getSagaIdentifierFrom(final Event event) {
        return delegateStep.getSagaIdentifierFrom(event);
    }

    @Override
    public Class<? extends Saga> getSagaClass() {
        return delegateStep.getSagaClass();
    }

    @Override
    public Class<? extends Step> getStepClass() {
        return delegateStep.getStepClass();
    }

    @Override
    public List<String> getActions() {
        List<String> actions = Lists.newArrayList(delegateStep.getActions());
        actions.add(getAction());
        return actions;
    }

    @Override
    public void clean(Object identifier) { }

    public Step getDelegateStep() {
        return delegateStep;
    }

    protected abstract String getAction();
}
