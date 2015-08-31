// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.step.Scheduler;
import com.viadeo.kasper.core.component.event.saga.step.Step;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SchedulingFacetApplier implements FacetApplier {

    private final Scheduler scheduler;

    public SchedulingFacetApplier(final Scheduler scheduler) {
        this.scheduler = checkNotNull(scheduler);
    }

    // ------------------------------------------------------------------------

    @Override
    public Step apply(final Method method, final Step step) {
        final XKasperSaga.Schedule scheduleAnnotation = method.getAnnotation(XKasperSaga.Schedule.class);
        final XKasperSaga.CancelSchedule cancelScheduleAnnotation = method.getAnnotation(XKasperSaga.CancelSchedule.class);
        final XKasperSaga.ScheduledByEvent scheduledByEventAnnotation = method.getAnnotation(XKasperSaga.ScheduledByEvent.class);

        final Set<Annotation> annotations = Sets.newHashSet();

        if (scheduleAnnotation != null) {
            annotations.add(scheduleAnnotation);
        }

        if (cancelScheduleAnnotation != null) {
            annotations.add(cancelScheduleAnnotation);
        }

        if (scheduledByEventAnnotation != null) {
            annotations.add(scheduledByEventAnnotation);
        }

        if (annotations.size() > 1) {
            throw new IllegalStateException(String.format("Should have one schedule annotation per step : %s", step.getSagaClass().getName()));
        }

        if (null != scheduleAnnotation) {
            return new SchedulingStep(scheduler, step, scheduleAnnotation);
        }

        if (null != cancelScheduleAnnotation) {
            return new SchedulingStep(scheduler, step, cancelScheduleAnnotation);
        }

        if (null != scheduledByEventAnnotation) {
            return new SchedulingStep(scheduler, step, scheduledByEventAnnotation);
        }

        return step;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
