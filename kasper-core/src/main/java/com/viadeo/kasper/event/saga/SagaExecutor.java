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
import com.viadeo.kasper.event.saga.step.Step;
import com.viadeo.kasper.event.saga.step.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaExecutor.class);

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
            LOGGER.error("No step associate in '{}' to the specified event : {}", getSagaClass().getSimpleName(), event.getClass().getName());
            return;
        }

        final Object sagaIdentifier = step.getSagaIdentifierFrom(event);
        final Saga saga;

        if (step instanceof Steps.StartStep) {
            saga = factory.create(sagaIdentifier, sagaClass);
        } else {
            final Optional<Saga> sagaOptional = repository.load(sagaIdentifier);
            if ( ! sagaOptional.isPresent()) {
                return;
            }
            saga = sagaOptional.get();
        }

        step.invoke(saga, event);

        // TODO save the state after invocation
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

    @VisibleForTesting
    protected Map<Class<?>, Step> getSteps() {
        return steps;
    }
}
