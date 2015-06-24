// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.jayway.awaitility.Awaitility;
import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.event.saga.repository.SagaRepository;
import com.viadeo.kasper.event.saga.spring.SagaConfiguration;
import org.junit.After;
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

import static com.viadeo.kasper.event.saga.TestFixture.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes =  {
                SagaConfiguration.class,
                SagaExecutorITest.TestConfiguration.class
        }
)
public class SagaExecutorITest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    public SagaManager sagaManager;

    @Inject
    public SagaRepository sagaRepository;

    @Mock
    public KasperCommandGateway commandGateway;

    public UUID identifier = UUID.randomUUID();

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
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(UUID.randomUUID()));
    }

    @Test
    public void execute_basic_step_on_no_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No available saga instance for the specified identifier");

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
        expectedException.expectMessage("Only one instance can be alive for the specified identifier");

        // When
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));
    }

    @Test
    public void execute_start_step_on_no_available_saga_instance_should_create_it() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaB(commandGateway));
        UUID identifier = UUID.randomUUID();

        // When
        sagaExecutor.execute(Contexts.empty(), new StartEvent(identifier));

        // Then
        assertTrue(sagaRepository.load(identifier).isPresent());
    }

    @Test
    public void execute_step_on_available_saga_instance_should_update_it() {
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
    public void execute_end_step_on_available_saga_instance_should_terminate_it() {
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
        expectedException.expectMessage("No available saga instance for the specified identifier");

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
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(Contexts.empty(), new EndEvent(identifier));
    }

    @Test
    public void execute_multiple_end_step_on_available_saga_instance_should_terminate_them() {
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

    @Configuration
    public static class TestConfiguration {
        @Bean
        public KasperCommandGateway commandGateway() {
            return mock(KasperCommandGateway.class);
        }
    }

}
