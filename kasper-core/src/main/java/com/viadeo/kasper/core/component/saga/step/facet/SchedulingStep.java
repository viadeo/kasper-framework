// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step.facet;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.core.component.saga.step.Step;
import com.viadeo.kasper.core.component.saga.step.StepInvocationException;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class SchedulingStep extends DecorateStep {

    protected interface SchedulingOperation {
        void execute(Object identifier);
        void clean(Object identifier);
        String getAction();
    }

    public static class CancelOperation implements SchedulingOperation {

        private final Scheduler scheduler;
        private final String methodName;
        private final Class<? extends Saga> sagaClass;
        private String action;

        public CancelOperation(final Scheduler scheduler, final Class<? extends Saga> sagaClass, final String methodName) {
            this.sagaClass = checkNotNull(sagaClass);
            this.scheduler = checkNotNull(scheduler);
            this.methodName = checkNotNull(methodName);
            this.action = String.format("Cancel(methodName=%s)", methodName);
        }

        @Override
        public void execute(final Object identifier) {
            scheduler.cancelSchedule(sagaClass, methodName, identifier);
        }

        @Override
        public String getAction() {
            return action;
        }

        @Override
        public void clean(Object identifier) { }
    }

    public static class ScheduleOperation implements SchedulingOperation {

        private final Scheduler scheduler;
        private final String methodName;
        private final Duration duration;
        private final Class<? extends Saga> sagaClass;
        private String action;

        public ScheduleOperation(final Scheduler scheduler, final Class<? extends Saga> sagaClass, final String methodName, final Long delay, final TimeUnit unit) {
            checkNotNull(delay);
            checkNotNull(unit);
            this.sagaClass = checkNotNull(sagaClass);
            this.scheduler = checkNotNull(scheduler);
            this.methodName = checkNotNull(methodName);
            this.duration = new Duration(TimeUnit.MILLISECONDS.convert(delay, unit));
            this.action = String.format("Schedule(delay=%s, unit=%s, methodName=%s)", delay, unit, methodName);
        }

        @Override
        public void execute(final Object identifier) {
            scheduler.schedule(sagaClass, methodName, identifier, duration);
        }

        @Override
        public String getAction() {
            return action;
        }

        @Override
        public void clean(Object identifier) {
            scheduler.cancelSchedule(sagaClass, methodName, identifier);
        }
    }

    // ------------------------------------------------------------------------

    private final SchedulingOperation operation;

    // ------------------------------------------------------------------------

    public SchedulingStep(
            final Scheduler scheduler,
            final Step delegateStep,
            final XKasperSaga.CancelSchedule annotation
    ) {
        this(delegateStep, new CancelOperation(
                scheduler,
                delegateStep.getSagaClass(),
                annotation.methodName()
        ));
    }

    public SchedulingStep(
            final Scheduler scheduler,
            final Step delegateStep,
            final XKasperSaga.Schedule annotation
    ) {
        this(delegateStep, new ScheduleOperation(
                scheduler,
                delegateStep.getSagaClass(),
                annotation.methodName(),
                annotation.delay(),
                annotation.unit()
        ));
    }

    public SchedulingStep(final Step delegateStep,
                          final SchedulingOperation operation) {
        super(delegateStep);
        this.operation = checkNotNull(operation);
    }

    // ------------------------------------------------------------------------

    @Override
    public void invoke(final Saga saga, final Context context, final Event event) throws StepInvocationException {
        getDelegateStep().invoke(saga, context, event);

        final Optional<Object> identifier = getSagaIdentifierFrom(event);
        if (identifier.isPresent()) {
            operation.execute(identifier.get());
        }
    }

    @Override
    protected String getAction() {
        return operation.getAction();
    }

    @Override
    public void clean(Object identifier) {
        operation.clean(identifier);
    }

}
