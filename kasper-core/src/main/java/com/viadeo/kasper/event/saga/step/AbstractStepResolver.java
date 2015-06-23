// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractStepResolver<ANNO extends Annotation> implements StepResolver {

    private final Class<ANNO> annotationClass;
    private final List<FacetApplier> facetAppliers;

    public AbstractStepResolver(final Class<ANNO> annotationClass, FacetApplier... facetAppliers) {
        checkNotNull(facetAppliers);
        this.facetAppliers = Lists.newArrayList(facetAppliers);
        this.annotationClass = checkNotNull(annotationClass);
    }

    @Override
    public Set<Step> resolve(final Class<? extends Saga> sagaClass) {
        checkNotNull(sagaClass);

        final Set<Step> steps = Sets.newHashSet();

        for (Method method : sagaClass.getMethods()) {
            final ANNO annotation = method.getAnnotation(annotationClass);

            if (annotation != null) {
                steps.add(applyFacets(method, createStep(method, annotation)));
            }
        }

        return steps;
    }

    protected Step applyFacets(Method method, Step step) {
        Step facetedStep = step;

        for (final FacetApplier faceting : facetAppliers) {
            facetedStep = faceting.apply(method, facetedStep);
        }

        return facetedStep;
    }

    public abstract Step createStep(final Method method, final ANNO annotation);
}
