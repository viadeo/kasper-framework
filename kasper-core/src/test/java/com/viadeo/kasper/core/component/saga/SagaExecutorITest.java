// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.jayway.awaitility.Awaitility;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.saga.factory.DefaultSagaFactoryProvider;
import com.viadeo.kasper.core.component.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.saga.spring.SagaConfiguration;
import com.viadeo.kasper.core.component.saga.step.Scheduler;
import com.viadeo.kasper.tools.ObjectMapperProvider;
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

import static com.viadeo.kasper.core.component.saga.TestFixture.*;
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
    }

    @After
    public void tearDown() throws Exception {
        sagaManager.clear();
        sagaRepository.delete(identifier);
    }

    @Test
    public void execute_end_step_on_no_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in loading saga : no available saga instance");

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(UUID.randomUUID()));
    }

    @Test
    public void execute_basic_step_on_no_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in loading saga : no available saga instance");

        // When
        sagaExecutor.execute(Contexts.empty(), new StepEvent(UUID.randomUUID()));
    }

    @Test
    public void execute_start_step_on_already_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in creating a saga : only one instance can be alive for a given identifier");

        // When
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));
    }

    @Test
    public void execute_start_step_on_no_available_saga_instance_should_create_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();

        // When
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));

        // Then
        assertTrue(sagaRepository.load(identifier).isPresent());
    }

    @Test
    public void execute_step_on_available_saga_instance_should_update_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new StepEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new StepEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new StepEvent(identifier));

        // Then
        TestSagaB saga = (TestSagaB) sagaRepository.load(identifier).get();
        assertEquals(3, saga.getCount());
    }

    @Test
    public void execute_end_step_on_available_saga_instance_should_terminate_it() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(identifier).isPresent());
    }

    @Test
    public void execute_basic_step_on_ended_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in loading saga : no available saga instance");

        // When
        sagaExecutor.execute(Contexts.empty(), new StepEvent(identifier));
    }

    @Test
    public void execute_end_step_on_ended_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Error in loading saga : no available saga instance");

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifier));
    }

    @Test
    public void execute_multiple_end_step_on_available_saga_instance_should_terminate_them() throws SagaPersistenceException {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifierSaga1 = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga1));
        UUID identifierSaga2 = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga2));

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifierSaga1));
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifierSaga2));

        // Then
        assertFalse(sagaRepository.load(identifierSaga1).isPresent());
        assertFalse(sagaRepository.load(identifierSaga2).isPresent());
    }

    @Test
    public void execute_step_will_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifierSaga = UUID.randomUUID();
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Unexpected error in invoking step");

        // When
        sagaExecutor.execute(Contexts.empty(), new ThrowExceptionEvent(identifierSaga));
    }

    @Test
    public void execute_a_scheduled_step_is_ok() throws InterruptedException {
        // Given
        final UUID identifierSaga = UUID.randomUUID();
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new StepEvent1(identifierSaga));

        //Then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Optional<Saga> sagaOptional = sagaRepository.load(identifierSaga);
                if (sagaOptional.isPresent()) {
                    TestSagaB saga = (TestSagaB) sagaOptional.get();
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
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new StepEvent1(identifierSaga));
        sagaExecutor.execute(Contexts.empty(), new StepEvent2(identifierSaga));

        //Then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Optional<Saga> sagaOptional = sagaRepository.load(identifierSaga);
                if (sagaOptional.isPresent()) {
                    TestSagaB saga = (TestSagaB) sagaOptional.get();
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
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifierSaga));
        sagaExecutor.execute(Contexts.empty(), new StepEvent3(identifierSaga));

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifierSaga));

        //Then
        assertFalse(sagaRepository.load(identifierSaga).isPresent());
        assertFalse(scheduler.isScheduled(TestSagaB.class, "invokedMethod", identifierSaga));
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
