// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class StepProcessor {

    private final List<StepResolver> resolvers;
    private final StepChecker checker;

    public StepProcessor() {
        this(
                new Steps.Checker(),
                new Steps.StartStepResolver(),
                new Steps.EndStepResolver(),
                new Steps.ScheduleStepResolver(),
                new Steps.BasicStepResolver()
        );
    }

    public StepProcessor(final StepChecker checker, final StepResolver... resolvers) {
        this.checker = checkNotNull(checker);
        this.resolvers = Arrays.asList(checkNotNull(resolvers));
    }

    public Set<Step> process(Class<? extends Saga> sagaClass) {
        checkNotNull(sagaClass);

        final Set<Step> steps = Sets.newHashSet();

        for (StepResolver resolver : resolvers) {
            steps.addAll(resolver.resolve(sagaClass));
        }

        checker.check(sagaClass, steps);

        return steps;
    }
}
