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

    private final SchedulingOperation operation;
    private final Step delegateStep;

    public SchedulingStep(
            final Scheduler scheduler,
            final Step delegateStep,
            final XKasperSaga.CancelSchedule annotation
    ) {
        this(delegateStep, new CancelOperation(scheduler, annotation.methodName()));
    }

    public SchedulingStep(
            final Scheduler scheduler,
            final Step delegateStep,
            final XKasperSaga.Schedule annotation
    ) {
        this(delegateStep, new ScheduleOperation(scheduler, annotation.methodName(), new Duration(TimeUnit.MILLISECONDS.convert(annotation.delay(), annotation.unit()))));
    }

    public SchedulingStep(final Step delegateStep,
                          final SchedulingOperation operation) {
        this.operation = checkNotNull(operation);
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
            operation.execute(getSagaClass(), identifier.get());
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

    protected interface SchedulingOperation {
        void execute(Class<? extends Saga> sagaClass, Object identifier);
    }

    protected static class CancelOperation implements SchedulingOperation {

        private final Scheduler scheduler;
        private final String methodName;

        public CancelOperation(final Scheduler scheduler, final String methodName) {
            this.scheduler = checkNotNull(scheduler);
            this.methodName = checkNotNull(methodName);
        }

        @Override
        public void execute(final Class<? extends Saga> sagaClass, final Object identifier) {
            scheduler.cancelSchedule(sagaClass, methodName, identifier);
        }
    }

    protected static class ScheduleOperation implements SchedulingOperation {

        private final Scheduler scheduler;
        private final String methodName;
        private final Duration duration;

        public ScheduleOperation(final Scheduler scheduler, final String methodName, final Duration duration) {
            this.scheduler = checkNotNull(scheduler);
            this.methodName = checkNotNull(methodName);
            this.duration = checkNotNull(duration);
        }

        @Override
        public void execute(final Class<? extends Saga> sagaClass, final Object identifier) {
            scheduler.schedule(sagaClass, methodName, identifier, duration);
        }
    }
}
