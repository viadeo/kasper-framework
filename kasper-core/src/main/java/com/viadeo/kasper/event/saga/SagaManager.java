// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.event.saga.step.Step;
import com.viadeo.kasper.event.saga.step.StepProcessor;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaManager {

    private final SagaFactory defaultSagaFactory;
    private final SagaRepository repository;
    private final StepProcessor operationProcessor;
    private final Map<Class<? extends Saga>, SagaExecutor> descriptors;

    public SagaManager(
            final SagaFactory defaultSagaFactory,
            final SagaRepository repository,
            final StepProcessor operationProcessor
    ) {
        this.operationProcessor = operationProcessor;
        this.defaultSagaFactory = checkNotNull(defaultSagaFactory);
        this.repository = checkNotNull(repository);
        this.descriptors = Maps.newHashMap();
    }

    public SagaExecutor register(final Saga saga) {
        checkNotNull(saga);

        final Class<? extends Saga> sagaClass = saga.getClass();

        Preconditions.checkState(
                ! descriptors.containsKey(sagaClass),
                String.format("The specified saga is already registered : %s", sagaClass.getName())
        );

        final SagaFactory factory = saga.getFactory().or(defaultSagaFactory);
        final Set<Step> steps = operationProcessor.process(sagaClass);
        final SagaExecutor executor = new SagaExecutor(sagaClass, steps, factory, repository);

        descriptors.put(sagaClass, executor);

        return executor;
    }

    public Optional<SagaExecutor> get(final Class<? extends Saga> sagaClass) {
        checkNotNull(sagaClass);
        return Optional.fromNullable(descriptors.get(sagaClass));
    }
}
