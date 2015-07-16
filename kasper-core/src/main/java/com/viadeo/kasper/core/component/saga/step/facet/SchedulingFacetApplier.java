// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step.facet;

import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.core.component.saga.step.Step;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.core.component.saga.step.Step;

import java.lang.reflect.Method;

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

        if ((null != scheduleAnnotation) && (null != cancelScheduleAnnotation)) {
            throw new IllegalStateException(String.format("Should have one schedule annotation per step : %s", step.getSagaClass().getName()));
        }

        if (null != scheduleAnnotation) {
            return new SchedulingStep(scheduler, step, scheduleAnnotation);
        }

        if (null != cancelScheduleAnnotation) {
            return new SchedulingStep(scheduler, step, cancelScheduleAnnotation);
        }

        return step;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }

}
