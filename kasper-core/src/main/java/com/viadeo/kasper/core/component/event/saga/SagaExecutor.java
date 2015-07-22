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
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manage the execution of saga's steps or methods. It is responsible
 * for redirecting published Events to the correct Saga instances. It will also manage the life cycle of
 * the Saga, based on these Events.
 */
public class SagaExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaExecutor.class);

    private final Class<? extends Saga> sagaClass;
    private final SagaFactory factory;
    private final SagaRepository repository;
    private final Map<Class<?>, Step> steps;

    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------

    public void execute(final Object sagaIdentifier, final String methodName) {
        checkNotNull(sagaIdentifier);
        checkNotNull(methodName);

        try {
            final Method method = sagaClass.getMethod(methodName);
            method.setAccessible(Boolean.TRUE);

            final Saga saga = getSaga(sagaIdentifier);

            try {
                method.invoke(saga);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(
                    "Unexpected error in executing saga method, <method={}> <saga={}> <identifier={}>",
                    methodName, sagaClass.getClass().getSimpleName(), sagaIdentifier, e
                );
            }

            try {
                repository.save(sagaIdentifier, saga);
            } catch (final SagaPersistenceException e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in executing saga method, <method=%s> <saga=%s> <identifier=%s>", methodName, sagaClass.getClass().getSimpleName(), sagaIdentifier),
                        e
                );
            }

        } catch (final NoSuchMethodException e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in executing saga method : unknown method name '%s', <saga=%s> <identifier=%s>", methodName, sagaClass.getClass().getSimpleName(), sagaIdentifier),
                    e
            );
        }
    }

    // ------------------------------------------------------------------------

    public void execute(final Context context, final Event event) {
        checkNotNull(event);
        checkNotNull(context);

        final Step step = steps.get(event.getClass());

        if (null == step) {
            throw new SagaExecutionException(
                String.format("No step defined in the saga '%s' to the specified event : %s", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Optional<Object> optionalSagaIdentifier = step.getSagaIdentifierFrom(event);

        if ( ! optionalSagaIdentifier.isPresent()) {
            throw new SagaExecutionException(
                String.format("Failed to retrieve saga identifier, <saga=%s> <event=%s>", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Object sagaIdentifier = optionalSagaIdentifier.get();
        final Saga saga = getOrCreateSaga(step, sagaIdentifier);

        try {
            step.invoke(saga, context, event);
        } catch (Exception e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in invoking step, <step=%s> <saga=%s> <identifier=%s>", step.name(), step.getSagaClass(), sagaIdentifier),
                    e
            );
        }

        persistSaga(step, sagaIdentifier, saga);
    }

    // ------------------------------------------------------------------------

    protected Saga getOrCreateSaga(final Step step, final Object sagaIdentifier) {

        if (Steps.StartStep.class.isAssignableFrom(step.getStepClass())) {
            try {
                if (repository.load(sagaIdentifier).isPresent()){
                    throw new SagaExecutionException(
                        String.format("Error in creating a saga : only one instance can be alive for a given identifier, <identifier=%s> <saga=%s> <step=%s>", sagaIdentifier, step.getSagaClass(), step.name())
                    );
                }
            } catch (SagaPersistenceException e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in creating a saga, <identifier=%s> <step=%s>", sagaIdentifier, step.name()),
                        e
                );
            }

            return factory.create(sagaIdentifier, sagaClass);
        }

        return getSaga(sagaIdentifier);
    }

    // ------------------------------------------------------------------------

    protected Saga getSaga(final Object sagaIdentifier) {
        try {
            final Optional<Saga> sagaOptional = repository.load(sagaIdentifier);

            if ( ! sagaOptional.isPresent()) {
                throw new SagaExecutionException(
                    String.format("Error in loading saga : no available saga instance, <identifier=%s>", sagaIdentifier)
                );
            }

            return sagaOptional.get();

        } catch (SagaPersistenceException e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in loading saga, <identifier=%s>", sagaIdentifier),
                    e
            );
        }
    }

    // ------------------------------------------------------------------------

    protected void persistSaga(final Step step, final Object sagaIdentifier, final Saga saga) {
        if (step instanceof Steps.EndStep) {
            try {
                repository.delete(sagaIdentifier);

                for (final Step aStep : steps.values()) {
                    aStep.clean(sagaIdentifier);
                }
            } catch (Exception e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in deleting saga, <saga=%s> <identifier=%s>", saga.getClass(), sagaIdentifier),
                        e
                );
            }

        } else {
            try {
                repository.save(sagaIdentifier, saga);
            } catch (SagaPersistenceException e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in saving saga, <saga=%s> <identifier=%s>", saga.getClass(), sagaIdentifier),
                        e
                );
            }
        }
    }

    // ------------------------------------------------------------------------

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

    protected Set<Class<?>> getEventClasses() {
        return steps.keySet();
    }

}
