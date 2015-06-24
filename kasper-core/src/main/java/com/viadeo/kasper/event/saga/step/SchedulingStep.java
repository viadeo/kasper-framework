// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class SchedulingStep implements Step {

    private final XKasperSaga.Schedule annotation;
    private final Step delegateStep;
    private final Scheduler scheduler;

    public SchedulingStep(
            final Scheduler scheduler,
            final Step delegateStep,
            final XKasperSaga.Schedule annotation
    ) {
        this.scheduler = checkNotNull(scheduler);
        this.annotation = checkNotNull(annotation);
        this.delegateStep = checkNotNull(delegateStep);
    }

    @Override
    public String name() {
        return delegateStep.name();
    }

    @Override
    public void invoke(Saga saga, Context context, Event event) throws StepInvocationException {
        delegateStep.invoke(saga, context, event);

        Optional<Object> identifier = getSagaIdentifierFrom(event);

        if (identifier.isPresent()) {
            scheduler.schedule(
                    getSagaClass(),
                    annotation.methodName(),
                    String.valueOf(identifier.get()),
                    new Duration(TimeUnit.MILLISECONDS.convert(annotation.delay(), annotation.unit()))
            );
        }
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
}
