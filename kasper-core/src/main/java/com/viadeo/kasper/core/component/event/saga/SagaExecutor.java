// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.codahale.metrics.Timer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manage the execution of saga's steps or methods. It is responsible
 * for redirecting published Events to the correct Saga instances. It will also manage the life cycle of
 * the Saga, based on these Events.
 *
 * @param <SAGA> the saga type
 */
public class SagaExecutor<SAGA extends Saga> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaExecutor.class);

    private final Class<SAGA> sagaClass;
    private final SagaFactory factory;
    private final SagaRepository repository;
    private final Map<Class<?>, Step> steps;

    // ------------------------------------------------------------------------

    public SagaExecutor(
            final Class<SAGA> sagaClass,
            final Set<Step> steps,
            final SagaFactory factory,
            final SagaRepository repository
    ) {
        this.sagaClass = checkNotNull(sagaClass);
        this.factory = checkNotNull(factory);
        this.repository = checkNotNull(repository);
        this.steps = Maps.newHashMap();

        for (final Step step : checkNotNull(steps)) {
            this.steps.put(step.getSupportedEvent().getEventClass(), step);
        }
    }

    // ------------------------------------------------------------------------

    public void execute(final Object sagaIdentifier, final String methodName, final boolean endAfterExecution) {
        checkNotNull(sagaIdentifier);
        checkNotNull(methodName);

        try {
            final Method method = sagaClass.getDeclaredMethod(methodName);
            method.setAccessible(Boolean.TRUE);

            final Optional<SAGA> optionalSaga = getSaga(sagaIdentifier);

            if ( ! optionalSaga.isPresent()) {
                LOGGER.error(
                        "Unexpected error in executing saga method : No saga available, <method={}> <saga={}> <identifier={}>",
                        methodName, sagaClass.getClass().getSimpleName(), sagaIdentifier
                );
                return;
            }

            final Saga saga = optionalSaga.get();

            invokeMethod(sagaIdentifier, methodName, method, saga);

            persistSaga(sagaIdentifier, saga, endAfterExecution);

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
                    String.format("No step defined in the optionalSaga '%s' to the specified event : %s", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Optional<Object> optionalSagaIdentifier = step.getSagaIdentifierFrom(event);

        if (!optionalSagaIdentifier.isPresent()) {
            throw new SagaExecutionException(
                    String.format("Failed to retrieve optionalSaga identifier, <optionalSaga=%s> <event=%s>", getSagaClass().getSimpleName(), event.getClass().getName())
            );
        }

        final Object sagaIdentifier = optionalSagaIdentifier.get();
        final Optional<SAGA> optionalSaga = getOrCreateSaga(step, sagaIdentifier);

        if (optionalSaga.isPresent()) {
            final Saga saga = optionalSaga.get();
            invokeStep(context, event, step, sagaIdentifier, saga);
            persistSaga(step, sagaIdentifier, saga);
        }
    }

    // ------------------------------------------------------------------------

    protected void invokeMethod(Object sagaIdentifier, String methodName, Method method, Saga saga) {
        try {
            method.invoke(saga);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(
                    "Unexpected error in executing saga method, <method={}> <saga={}> <identifier={}>",
                    methodName, sagaClass.getClass().getSimpleName(), sagaIdentifier, e
            );
        }
    }

    // ------------------------------------------------------------------------

    protected void invokeStep(Context context, Event event, Step step, Object sagaIdentifier, Saga saga) {
        try {
            step.invoke(saga, context, event);
        } catch (Exception e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in invoking step, <step=%s> <saga=%s> <identifier=%s>", step.name(), step.getSagaClass(), sagaIdentifier),
                    e
            );
        }
    }

    // ------------------------------------------------------------------------

    protected Optional<SAGA> getOrCreateSaga(final Step step, final Object sagaIdentifier) {

        if (Steps.StartStep.class.isAssignableFrom(step.getStepClass())) {
            try {
                if (repository.load(step.getSagaClass(), sagaIdentifier).isPresent()) {
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
            final SAGA saga = factory.create(sagaIdentifier, sagaClass);
            persistSaga(step, sagaIdentifier, saga);
            return Optional.of(saga);
        }

        return getSaga(sagaIdentifier);
    }

    // ------------------------------------------------------------------------

    public Optional<SAGA> getSaga(final Object sagaIdentifier) {
        try {
            return repository.load(this.sagaClass, sagaIdentifier);

        } catch (SagaPersistenceException e) {
            throw new SagaExecutionException(
                    String.format("Unexpected error in loading saga, <identifier=%s>", sagaIdentifier),
                    e
            );
        }
    }

    // ------------------------------------------------------------------------

    protected void persistSaga(final Step step, final Object sagaIdentifier, final Saga saga) {
        persistSaga(sagaIdentifier, saga, step instanceof Steps.EndStep);
    }

    protected void persistSaga(final Object sagaIdentifier, final Saga saga, final boolean endSaga) {
        long startTime = System.nanoTime();
        if (endSaga) {
            Timer.Context endTimer = KasperMetrics.getMetricRegistry().timer(KasperMetrics.name(saga.getClass(), "end-handle-time")).time();
            try {
                repository.delete(saga.getClass(), sagaIdentifier);

                for (final Step aStep : steps.values()) {
                    Timer.Context endTimerSteps = KasperMetrics.getMetricRegistry().timer(KasperMetrics.name(saga.getClass(), saga.getClass().getSimpleName() + "." + aStep.name() + ".end-handle-time")).time();
                    aStep.clean(sagaIdentifier);
                    endTimerSteps.stop();
                }
            } catch (Exception e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in deleting saga, <saga=%s> <identifier=%s>", saga.getClass(), sagaIdentifier),
                        e
                );
            } finally {
                long endTime = System.nanoTime();
                LOGGER.info(String.format("[SagaExecutor][End] Process time %d ms", (endTime - startTime)/1000000));
                endTimer.stop();
            }

        } else {
            try {
                repository.save(sagaIdentifier, saga);
            } catch (SagaPersistenceException e) {
                throw new SagaExecutionException(
                        String.format("Unexpected error in saving saga, <saga=%s> <identifier=%s>", saga.getClass(), sagaIdentifier),
                        e
                );
            }finally {
                long endTime = System.nanoTime();
                LOGGER.info(String.format("[SagaExecutor][Save] Process time %d ms", (endTime - startTime)/1000000));
            }
        }
    }

    // ------------------------------------------------------------------------

    public Class getSagaClass() {
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

    protected Set<EventDescriptor> getEventClasses() {
        return Sets.newHashSet(Collections2.transform(steps.values(), new Function<Step, EventDescriptor>() {
            @Override
            public EventDescriptor apply(@Nullable Step input) {
                return input.getSupportedEvent();
            }
        }));
    }

}
