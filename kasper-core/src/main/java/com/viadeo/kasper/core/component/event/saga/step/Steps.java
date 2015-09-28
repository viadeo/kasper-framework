// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.viadeo.kasper.api.component.event.SchedulableSagaMethod;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;
import com.viadeo.kasper.core.component.event.saga.step.facet.SchedulingStep;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public final class Steps {

    private Steps() { /* utility class */ }

    // ------------------------------------------------------------------------

    public static class Checker implements StepChecker {

        @Override
        public void check(final Class<? extends Saga> sagaClass, final Set<Step> steps) {
            checkState(
                steps.size() > 0,
                String.format("Should define at less two step methods (start/end) : %s", sagaClass.getName())
            );

            for (final Step step : steps) {
                if(step instanceof SchedulingStep) {
                    SchedulingStep schedulingStep = (SchedulingStep) step;
                    checkState(
                            !(schedulingStep.getDelegateStep() instanceof EndStep),
                            String.format("Should not use scheduling step on an end step : %s", sagaClass.getName())
                    );

                }
            }

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

            final Multimap<EventDescriptor,Step> stepsBySupportedEvent = Multimaps.index(steps, new Function<Step, EventDescriptor>() {
                @Override
                public EventDescriptor apply(Step input) {
                    return input.getSupportedEvent();
                }
            });

            // TODO check inheritance of events

            for (final EventDescriptor eventClass : stepsBySupportedEvent.keySet()) {
                Collection<Step> stepCollection = stepsBySupportedEvent.get(eventClass);
                checkState(
                    stepCollection.size() == 1,
                    String.format("Should handle an event type per step : <saga=%s> <steps=%s>", sagaClass.getName(), stepCollection)
                );
            }

            final Multimap<Class<? extends Step>,Step> stepsByTypes = Multimaps.index(steps, new Function<Step, Class<? extends Step>>() {
                @Override
                public Class<? extends Step> apply(Step input) {
                return input.getStepClass();
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

            final Collection<Step> schedulingByEventSteps = Collections2.filter(steps, new Predicate<Step>() {
                @Override
                public boolean apply(Step input) {
                    return SchedulingStep.class.isAssignableFrom(input.getClass()) &&
                            ((SchedulingStep) input).getOperationType() == SchedulingStep.OperationType.SCHEDULE_BY_EVENT;
                }
            });

            for (final Step step : schedulingByEventSteps) {
                checkState(
                        SchedulableSagaMethod.class.isAssignableFrom(step.getSupportedEvent().getEventClass()),
                        String.format("The event should be assignment-compatible with '%s' : <saga=%s>", SchedulableSagaMethod.class.getName(), sagaClass.getName())
                );
            }
        }
    }

    // ------------------------------------------------------------------------

    public static class StartStepResolver extends AbstractStepResolver<XKasperSaga.Start> {

        public StartStepResolver(final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.Start.class, facetApplierRegistry);
        }

        @Override
        public Step createStep(final Method method, final XKasperSaga.Start annotation, final SagaIdReconciler idReconciler) {
            return new StartStep(method, annotation.getter(), idReconciler);
        }
    }

    // ------------------------------------------------------------------------

    public static class EndStepResolver extends AbstractStepResolver<XKasperSaga.End> {

        public EndStepResolver(final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.End.class, facetApplierRegistry);
        }

        @Override
        public Step createStep(final Method method, final XKasperSaga.End annotation, final SagaIdReconciler idReconciler) {
            return new EndStep(method, annotation.getter(), idReconciler);
        }
    }

    // ------------------------------------------------------------------------

    public static class BasicStepResolver extends AbstractStepResolver<XKasperSaga.Step> {

        public BasicStepResolver(final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.Step.class, facetApplierRegistry);
        }

        @Override
        public Step createStep(final Method method, final XKasperSaga.Step annotation, final SagaIdReconciler idReconciler) {
            return new BasicStep(method, annotation.getter(), idReconciler);
        }
    }

    // ------------------------------------------------------------------------

    public static class StartStep extends BaseStep {
        public StartStep(final Method method, final String getterName, final SagaIdReconciler idReconciler) {
            super(method, "Start", getterName, idReconciler);
        }
    }

    public static class EndStep extends BaseStep {
        public EndStep(final Method method, final String getterName, final SagaIdReconciler idReconciler) {
            super(method, "End", getterName, idReconciler);
        }
    }

    public static class BasicStep extends BaseStep {
        public BasicStep(final Method method, final String getterName, final SagaIdReconciler idReconciler) {
            super(method, "Step", getterName, idReconciler);
        }
    }

}
