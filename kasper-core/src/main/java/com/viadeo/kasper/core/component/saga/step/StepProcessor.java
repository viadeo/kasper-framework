// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.SagaIdReconciler;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class StepProcessor {

    private final List<StepResolver> resolvers;
    private final StepChecker checker;

    // ------------------------------------------------------------------------

    public StepProcessor(final StepResolver... resolvers) {
        this(new Steps.Checker(), resolvers);
    }

    // ------------------------------------------------------------------------

    public StepProcessor(final StepChecker checker, final StepResolver... resolvers) {
        this.checker = checkNotNull(checker);
        this.resolvers = Arrays.asList(checkNotNull(resolvers));
    }

    public Set<Step> process(final Class<? extends Saga> sagaClass, final SagaIdReconciler sagaIdReconciler) {
        checkNotNull(sagaClass);

        final Set<Step> steps = Sets.newHashSet();
        for (final StepResolver resolver : resolvers) {
            steps.addAll(resolver.resolve(sagaClass, sagaIdReconciler));
        }

        checker.check(sagaClass, steps);
        return steps;
    }

}
