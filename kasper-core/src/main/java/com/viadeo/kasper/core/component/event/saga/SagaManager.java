// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class SagaManager {

    private final SagaFactoryProvider sagaFactoryProvider;
    private final SagaRepository repository;
    private final StepProcessor operationProcessor;
    private final Map<Class<? extends Saga>, SagaExecutor> descriptors;

    // ------------------------------------------------------------------------

    public SagaManager(
            final SagaFactoryProvider sagaFactoryProvider,
            final SagaRepository repository,
            final StepProcessor operationProcessor
    ) {
        this.operationProcessor = checkNotNull(operationProcessor);
        this.sagaFactoryProvider = checkNotNull(sagaFactoryProvider);
        this.repository = checkNotNull(repository);
        this.descriptors = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    public SagaExecutor register(final Saga saga) {
        checkNotNull(saga);

        final Class<? extends Saga> sagaClass = saga.getClass();

        checkState(
            !descriptors.containsKey(sagaClass),
            String.format("The specified saga is already registered : %s", sagaClass.getName())
        );

        final SagaFactory factory = checkNotNull(sagaFactoryProvider.getOrCreate(saga));
        final SagaIdReconciler reconciler = saga.getIdReconciler().or(SagaIdReconciler.NONE);
        final Set<Step> steps = operationProcessor.process(sagaClass, reconciler);
        final SagaExecutor executor = new SagaExecutor(sagaClass, steps, factory, repository);

        descriptors.put(sagaClass, executor);

        return executor;
    }

    public Optional<SagaExecutor> get(final Class<? extends Saga> sagaClass) {
        checkNotNull(sagaClass);
        return Optional.fromNullable(descriptors.get(sagaClass));
    }

    @VisibleForTesting
    public void clear() {
        descriptors.clear();
    }

}
