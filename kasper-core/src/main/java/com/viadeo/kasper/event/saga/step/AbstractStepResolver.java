// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaIdReconciler;
import com.viadeo.kasper.event.saga.step.facet.FacetApplier;
import com.viadeo.kasper.event.saga.step.facet.FacetApplierRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractStepResolver<ANNO extends Annotation> implements StepResolver {

    private final Class<ANNO> annotationClass;
    private final FacetApplierRegistry facetApplierRegistry;

    // ------------------------------------------------------------------------

    public AbstractStepResolver(final Class<ANNO> annotationClass, final FacetApplierRegistry facetApplierRegistry) {
        this.facetApplierRegistry = checkNotNull(facetApplierRegistry);
        this.annotationClass = checkNotNull(annotationClass);
    }

    @Override
    public Set<Step> resolve(final Class<? extends Saga> sagaClass, final SagaIdReconciler idReconciler) {
        checkNotNull(sagaClass);

        final Set<Step> steps = Sets.newHashSet();

        for (final Method method : sagaClass.getMethods()) {
            final ANNO annotation = method.getAnnotation(annotationClass);
            if (null != annotation) {
                steps.add(applyFacets(method, createStep(method, annotation, idReconciler)));
            }
        }

        return steps;
    }

    protected Step applyFacets(final Method method, final Step step) {

        Step facetedStep = step;
        for (final FacetApplier faceting : facetApplierRegistry.list()) {
            facetedStep = faceting.apply(method, facetedStep);
        }

        return facetedStep;
    }

    // ------------------------------------------------------------------------

    public abstract Step createStep(Method method, ANNO annotation, SagaIdReconciler idReconciler);

}
