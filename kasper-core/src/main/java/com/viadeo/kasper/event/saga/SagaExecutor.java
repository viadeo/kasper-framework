// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.event.saga.step.Step;
import com.viadeo.kasper.event.saga.step.Steps;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaExecutor {

    private final Class<? extends Saga> sagaClass;
    private final SagaFactory factory;
    private final SagaRepository repository;
    private final Map<Class<?>, Step> steps;

    public SagaExecutor(
            final Class<? extends Saga> sagaClass,
            final Set<Step> steps,
            final SagaFactory factory,
            final SagaRepository repository
    ) {
        this.sagaClass = checkNotNull(sagaClass);
        this.factory = checkNotNull(factory);
        this.repository = checkNotNull(repository);
        this.steps = Maps.newHashMap();

        for (final Step step : checkNotNull(steps)) {
            this.steps.put(step.getSupportedEvent(), step);
        }
    }

    public void execute(final Event event) {
        checkNotNull(event);

        final Step step = steps.get(event.getClass());

        if (step == null) {
            throw new SagaExecutionException(
                    String.format("No step associate in '%s' to the specified event : %s", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Optional<Object> optionalSagaIdentifier = step.getSagaIdentifierFrom(event);

        if ( ! optionalSagaIdentifier.isPresent()) {
            throw new SagaExecutionException(
                    String.format("Failed to retrieve saga identifier, <saga=%s> <event=%s>", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Object sagaIdentifier = optionalSagaIdentifier.get();
        final Saga saga;

        if (step instanceof Steps.StartStep) {
            if(repository.load(sagaIdentifier).isPresent()){
                throw new SagaExecutionException(
                        String.format("Only one instance can be alive for the specified identifier '%s'", sagaIdentifier)
                );
            }
            saga = factory.create(sagaIdentifier, sagaClass);
        } else {
            final Optional<Saga> sagaOptional = repository.load(sagaIdentifier);

            if ( ! sagaOptional.isPresent()) {
                throw new SagaExecutionException(
                        String.format("No available saga instance for the specified identifier '%s'", sagaIdentifier)
                );
            }

            saga = sagaOptional.get();
        }

        try {
            step.invoke(saga, event);
        } catch (Exception e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in invoking step, <saga=%s> <identifier=%s>", saga.getClass().getSimpleName(), sagaIdentifier),
                    e
            );
        }

        if (step instanceof Steps.EndStep) {
            repository.delete(sagaIdentifier);
        } else {
            repository.save(sagaIdentifier, saga);
        }

    }

    public Class<? extends Saga> getSagaClass() {
        return sagaClass;
    }

    @VisibleForTesting
    protected SagaFactory getSagaFactory() {
        return factory;
    }

    @VisibleForTesting
    protected SagaRepository getSagaRepository() {
        return repository;
    }
}
