// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.jayway.awaitility.Awaitility;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.DefaultSagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Scheduler;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes =  {
                SagaExecutorITest.TestConfiguration.class,
                MetricRegistry.class,
                SagaConfiguration.class
        }
)
public class SagaExecutorITest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    public SagaManager sagaManager;

    @Inject
    public SagaRepository sagaRepository;

    @Inject
    public Scheduler scheduler;

    @Mock
    public KasperCommandGateway commandGateway;

    public UUID identifier = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        DefaultSagaFactoryProvider.clearCache();
        if(!scheduler.isInitialized()) {
            scheduler.initialize();
        }
    }

    @After
    public void tearDown() throws Exception {
        sagaManager.clear();
        sagaRepository.delete(TestFixture.TestSagaB.class, identifier);
    }

    @Test
    public void execute_end_step_on_no_available_saga_instance_should_not_find_saga() throws SagaPersistenceException {
        // Given
        UUID identifier = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_basic_step_on_no_available_saga_instance_should_not_find_saga() throws SagaPersistenceException {
        // Given
        UUID identifier = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_start_step_on_already_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in creating a saga : only one instance can be alive for a given identifier");

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));
    }

    @Test
    public void execute_start_step_on_no_available_saga_instance_should_create_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));

        // Then
        assertTrue(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_step_on_available_saga_instance_should_update_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent(identifier));

        // Then
        TestFixture.TestSagaB saga = sagaRepository.load(TestFixture.TestSagaB.class, identifier).get();
        assertEquals(3, saga.getCount());
    }

    @Test
    public void execute_end_step_on_available_saga_instance_should_terminate_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_scheduled_method_with_end_on_available_saga_instance_should_terminate_it() throws SagaPersistenceException, InterruptedException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent5(identifier));
        Thread.sleep(300L);

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_basic_step_on_ended_saga_instance_should_not_find_saga() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_end_step_on_ended_saga_instance_should_not_find_saga() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifier).isPresent());
    }

    @Test
    public void execute_multiple_end_step_on_available_saga_instance_should_terminate_them() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifierSaga1 = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga1));
        UUID identifierSaga2 = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga2));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifierSaga1));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifierSaga2));

        // Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga1).isPresent());
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga2).isPresent());
    }

    @Test
    public void execute_step_will_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        UUID identifierSaga = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Unexpected error in invoking step");

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.ThrowExceptionEvent(identifierSaga));
    }

    @Test
    public void execute_a_scheduled_step_is_ok() throws InterruptedException {
        // Given
        final UUID identifierSaga = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent1(identifierSaga));

        //Then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Optional<TestFixture.TestSagaB> sagaOptional = sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga);
                if (sagaOptional.isPresent()) {
                    TestFixture.TestSagaB saga = sagaOptional.get();
                    return 1 == saga.getInvokedMethodCount();
                }
                return Boolean.FALSE;
            }
        });
    }

    @Test
    public void execute_a_scheduled_step_by_an_event_is_ok() throws InterruptedException {
        // Given
        final UUID identifierSaga = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent4(identifierSaga, DateTime.now().plusMillis(200)));

        //Then
        Awaitility.await().atMost(20, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Optional<TestFixture.TestSagaB> sagaOptional = sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga);
                if (sagaOptional.isPresent()) {
                    TestFixture.TestSagaB saga = sagaOptional.get();
                    return 1 == saga.getInvokedMethodCount();
                }
                return Boolean.FALSE;
            }
        });
    }

    @Test
    public void execute_a_cancel_scheduled_step_is_ok() throws InterruptedException {
        // Given
        final UUID identifierSaga = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent1(identifierSaga));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent2(identifierSaga));

        //Then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Optional<TestFixture.TestSagaB> sagaOptional = sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga);
                if (sagaOptional.isPresent()) {
                    TestFixture.TestSagaB saga = sagaOptional.get();
                    return 0 == saga.getInvokedMethodCount();
                }
                return Boolean.FALSE;
            }
        });
    }

    @Test
    public void execute_end_step_on_saga_instance_for_which_we_have_some_scheduled_invocation() throws InterruptedException, SagaPersistenceException {
        // Given
        final UUID identifierSaga = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestFixture.TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StartEvent(identifierSaga));
        sagaExecutor.execute(Contexts.empty(), new TestFixture.StepEvent3(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new TestFixture.EndEvent(identifierSaga));

        //Then
        assertFalse(sagaRepository.load(TestFixture.TestSagaB.class, identifierSaga).isPresent());
        assertFalse(scheduler.isScheduled(TestFixture.TestSagaB.class, "invokedMethod", identifierSaga));
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public KasperCommandGateway commandGateway() {
            return mock(KasperCommandGateway.class);
        }
        @Bean
        public ObjectMapper objectMapper() {
            return ObjectMapperProvider.INSTANCE.mapper();
        }
    }

}
