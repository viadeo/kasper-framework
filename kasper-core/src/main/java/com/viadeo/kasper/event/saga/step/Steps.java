// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;

import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// TODO adds metrics for each steps
public final class Steps {

    private Steps() {}

    public static class Checker implements StepChecker {
        @Override
        public void check(final Class<? extends Saga> sagaClass, final Set<Step> steps) {
            checkState(
                    steps.size() > 0,
                    String.format("Should define at less two step methods (start/end) : %s", sagaClass.getName())
            );

            final Multimap<String,Step> stepsByName = Multimaps.index(steps, new Function<Step, String>() {
                @Override
                public String apply(Step input) {
                    return input.name();
                }
            });

            for (final String methodName : stepsByName.keySet()) {
                checkState(
                        stepsByName.get(methodName).size() == 1,
                        String.format("Should annotate the method '%s' by only one annotation step : %s", methodName, sagaClass.getName())
                );
            }

            final Multimap<Class<? extends Event>,Step> stepsBySupportedEvent = Multimaps.index(steps, new Function<Step, Class<? extends Event>>() {
                @Override
                public Class<? extends Event> apply(Step input) {
                    return input.getSupportedEvent();
                }
            });

            // TODO check inheritance of events

            for (final Class<? extends Event> eventClass : stepsBySupportedEvent.keySet()) {
                checkState(
                        stepsBySupportedEvent.get(eventClass).size() == 1,
                        String.format("Should handle an event type per step : %s", sagaClass.getName())
                );
            }

            final Multimap<Class<? extends Step>,Step> stepsByTypes = Multimaps.index(steps, new Function<Step, Class<? extends Step>>() {
                @Override
                public Class<? extends Step> apply(Step input) {
                    return input.getClass();
                }
            });

            checkState(
                    stepsByTypes.get(StartStep.class).size() == 1,
                    String.format("Should have one start method : %s", sagaClass.getName())
            );
            checkState(
                    stepsByTypes.get(EndStep.class).size() >= 1,
                    String.format("Should have at less one end method : %s", sagaClass.getName())
            );
        }
    }

    public static class StartStepResolver implements StepResolver {
        @Override
        public Set<Step> resolve(final Class<? extends Saga> sagaClass) {
            checkNotNull(sagaClass);

            final Set<Step> steps = Sets.newHashSet();

            for (Method method : sagaClass.getMethods()) {
                XKasperSaga.Start annotation = method.getAnnotation(XKasperSaga.Start.class);
                if (annotation != null) {
                    steps.add(new StartStep(method, annotation.getter()));
                }
            }

            return steps;
        }
    }

    public static class EndStepResolver implements StepResolver {
        @Override
        public Set<Step> resolve(final Class<? extends Saga> sagaClass) {
            checkNotNull(sagaClass);

            final Set<Step> steps = Sets.newHashSet();

            for (Method method : sagaClass.getMethods()) {
                XKasperSaga.End annotation = method.getAnnotation(XKasperSaga.End.class);
                if (annotation != null) {
                    steps.add(new EndStep(method, annotation.getter()));
                }
            }

            return steps;
        }
    }

    public static class BasicStepResolver implements StepResolver {
        @Override
        public Set<Step> resolve(final Class<? extends Saga> sagaClass) {
            checkNotNull(sagaClass);

            final Set<Step> steps = Sets.newHashSet();

            for (Method method : sagaClass.getMethods()) {
                XKasperSaga.Step annotation = method.getAnnotation(XKasperSaga.Step.class);
                if (annotation != null) {
                    steps.add(new BasicStep(method, annotation.getter()));
                }
            }

            return steps;
        }
    }

    public static class ScheduleStepResolver implements StepResolver {
        @Override
        public Set<Step> resolve(final Class<? extends Saga> sagaClass) {
            checkNotNull(sagaClass);

            final Set<Step> steps = Sets.newHashSet();

            for (Method method : sagaClass.getMethods()) {
                XKasperSaga.Schedule annotation = method.getAnnotation(XKasperSaga.Schedule.class);
                if (annotation != null) {
                    steps.add(new ScheduleStep(method, annotation.getter()));
                }
            }

            return steps;
        }
    }

    public static class StartStep extends BaseStep {
        public StartStep(final Method method, final String getterName) {
            super(method, getterName);
        }

    }

    public static class EndStep extends BaseStep {
        public EndStep(final Method method, final String getterName) {
            super(method, getterName);
        }
    }

    public static class BasicStep extends BaseStep {
        public BasicStep(final Method method, final String getterName) {
            super(method, getterName);
        }
    }

    public static class ScheduleStep extends BaseStep {
        public ScheduleStep(final Method method, final String getterName) {
            super(method, getterName);
        }
    }
}
