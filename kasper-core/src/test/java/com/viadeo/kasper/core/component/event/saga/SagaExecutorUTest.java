// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.viadeo.kasper.core.component.event.saga.TestFixture.*;
import static org.mockito.Mockito.*;

public class SagaExecutorUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SagaFactory factory;
    private SagaRepository repository;
    private SagaExecutor executor;
    private Step startStep;
    private Step basicStep;

    @Before
    public void setUp() throws Exception {
        SagaIdReconciler idReconciler = SagaIdReconciler.NONE;
        factory = mock(SagaFactory.class);
        repository = mock(SagaRepository.class);
        startStep = spy(new Steps.StartStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle", TestFixture.TestEvent.class), "getId", idReconciler));
        basicStep = spy(new Steps.BasicStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle2", TestFixture.TestEvent2.class), "getId", idReconciler));
        executor = new SagaExecutor(
                TestFixture.TestSagaA.class,
                Sets.<Step>newHashSet(startStep, basicStep),
                factory,
                repository
        );
    }

    @Test
    public void execute_withUnknownEvent_isOK() {
        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No step defined in the optionalSaga 'TestSagaA' to the specified event");

        // When
        executor.execute(Contexts.empty(), new Event() { });
    }

    @Test
    public void execute_withAnEventAssociatedToAStartStep_createNewSaga() throws SagaPersistenceException {
        // Given
        TestFixture.TestEvent event = new TestFixture.TestEvent("2015");
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        Context context = Contexts.empty();
        when(factory.create("2015", TestFixture.TestSagaA.class)).thenReturn(saga);
        when(repository.load("2015")).thenReturn(Optional.<Saga>absent());

        // When
        executor.execute(context, event);

        // Then
        verify(factory).create("2015", TestFixture.TestSagaA.class);
        verify(startStep).invoke(saga, context, event);
    }

    @Test
    public void execute_withAnEventAssociatedToAStepExceptAStart_loadSaga() throws SagaPersistenceException {
        // Given
        TestFixture.TestEvent2 event = new TestFixture.TestEvent2("2015");
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        Context context = Contexts.empty();
        when(repository.load("2015")).thenReturn(Optional.<Saga>of(saga));

        // When
        executor.execute(context, event);

        // Then
        verify(factory, never()).create("2015", TestFixture.TestSagaA.class);
        verify(repository).load("2015");
        verify(basicStep).invoke(saga, context, event);
    }
}
