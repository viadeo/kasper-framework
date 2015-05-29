// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.saga.step.Step;
import com.viadeo.kasper.event.saga.step.Steps;
import com.viadeo.kasper.event.saga.step.TestSaga;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SagaExecutorUTest {

    private SagaFactory factory;
    private SagaRepository repository;
    private SagaExecutor executor;
    private Step startStep;
    private Step basicStep;

    @Before
    public void setUp() throws Exception {
        factory = mock(SagaFactory.class);
        repository = mock(SagaRepository.class);
        startStep = spy(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));
        basicStep = spy(new Steps.BasicStep(TestSaga.getMethod("handle2", TestSaga.TestEvent2.class), "getId"));
        executor = new SagaExecutor(
                TestSaga.class,
                Sets.<Step>newHashSet(startStep, basicStep),
                factory,
                repository
        );
    }

    @Test
    public void execute_withUnknownEvent_isOK() {
        // When
        executor.execute(new Event() { });

        // Then
        verifyZeroInteractions(factory, repository);
    }

    @Test
    public void execute_withAnEventAssociatedToAStartStep_createNewSaga() {
        // Given
        TestSaga.TestEvent event = new TestSaga.TestEvent("2015");
        TestSaga saga = new TestSaga();
        when(factory.create("2015", TestSaga.class)).thenReturn(saga);

        // When
        executor.execute(event);

        // Then
        verify(factory).create("2015", TestSaga.class);
        verify(repository, never()).load("2015");
        verify(startStep).invoke(saga, event);
    }

    @Test
    public void execute_withAnEventAssociatedToAStepExceptAStart_loadSaga() {
        // Given
        TestSaga.TestEvent2 event = new TestSaga.TestEvent2("2015");
        TestSaga saga = new TestSaga();
        when(repository.load("2015")).thenReturn(Optional.<Saga>of(saga));

        // When
        executor.execute(event);

        // Then
        verify(factory, never()).create("2015", TestSaga.class);
        verify(repository).load("2015");
        verify(basicStep).invoke(saga, event);
    }
}