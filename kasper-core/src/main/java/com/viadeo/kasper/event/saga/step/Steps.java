// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;

import java.lang.reflect.Method;
import java.util.Collection;
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
                Collection<Step> stepCollection = stepsBySupportedEvent.get(eventClass);
                checkState(
                        stepCollection.size() == 1,
                        String.format("Should handle an event type per step : <saga=%s> <steps=%s>", sagaClass.getName(), stepCollection)
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

    public static class StartStepResolver extends AbstractStepResolver<XKasperSaga.Start> {

        private final MetricRegistry metricRegistry;

        public StartStepResolver(final MetricRegistry metricRegistry, final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.Start.class, facetApplierRegistry);
            this.metricRegistry = checkNotNull(metricRegistry);
        }

        @Override
        public Step createStep(final Method method, final XKasperSaga.Start annotation) {
            return new MeasureStep(metricRegistry, new StartStep(method, annotation.getter()));
        }
    }

    public static class EndStepResolver extends AbstractStepResolver<XKasperSaga.End> {

        private final MetricRegistry metricRegistry;

        public EndStepResolver(final MetricRegistry metricRegistry, final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.End.class, facetApplierRegistry);
            this.metricRegistry = checkNotNull(metricRegistry);
        }

        @Override
        public Step createStep(final Method method, final XKasperSaga.End annotation) {
            return new MeasureStep(metricRegistry, new EndStep(method, annotation.getter()));
        }
    }

    public static class BasicStepResolver extends AbstractStepResolver<XKasperSaga.Step> {

        public BasicStepResolver(final FacetApplierRegistry facetApplierRegistry) {
            super(XKasperSaga.Step.class, facetApplierRegistry);
        }

        @Override
        public Step createStep(Method method, XKasperSaga.Step annotation) {
            return new BasicStep(method, annotation.getter());
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

    public static class MeasureStep implements Step {

        private final MetricRegistry metricRegistry;
        private final Step delegateStep;
        private final String metricName;

        public MeasureStep(final MetricRegistry metricRegistry, final Step delegateStep) {
            this.delegateStep = checkNotNull(delegateStep);
            this.metricRegistry = checkNotNull(metricRegistry);
            this.metricName = KasperMetrics.name(
                    MetricNameStyle.DOMAIN_TYPE_COMPONENT, delegateStep.getSagaClass(), delegateStep.getClass().getSimpleName().replace("Step", "").toLowerCase()
            );
        }

        public String getMetricName() {
            return metricName;
        }

        @Override
        public String name() {
            return delegateStep.name();
        }

        @Override
        public void invoke(Saga saga, Context context, Event event) throws StepInvocationException {
            try {
                delegateStep.invoke(saga, context, event);
            } finally {
                metricRegistry.meter(metricName).mark();
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

    public static class BasicStep extends BaseStep {
        public BasicStep(final Method method, final String getterName) {
            super(method, getterName);
        }
    }
}
