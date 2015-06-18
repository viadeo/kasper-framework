package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.exception.SagaExecutionException;
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
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(new EndEvent(UUID.randomUUID()));
    }

    @Test
    public void execute_basic_step_on_no_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(new StepEvent(UUID.randomUUID()));
    }

    @Test
    public void execute_start_step_on_already_available_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Only one instance can be alive for the specified identifier");

        // When
        sagaExecutor.execute(new StartEvent(identifier));
    }

    @Test
    public void execute_start_step_on_no_available_saga_instance_should_create_it() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();

        // When
        sagaExecutor.execute(new StartEvent(identifier));

        // Then
        assertTrue(sagaRepository.load(identifier).isPresent());
    }

    @Test
    public void execute_step_on_available_saga_instance_should_update_it() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifier));

        // When
        sagaExecutor.execute(new StepEvent(identifier));
        sagaExecutor.execute(new StepEvent(identifier));
        sagaExecutor.execute(new StepEvent(identifier));

        // Then
        TestSaga saga = (TestSaga) sagaRepository.load(identifier).get();
        assertEquals(3, saga.getCount());
    }

    @Test
    public void execute_end_step_on_available_saga_instance_should_terminate_it() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifier));

        // When
        sagaExecutor.execute(new EndEvent(identifier));

        // Then
        assertFalse(sagaRepository.load(identifier).isPresent());
    }

    @Test
    public void execute_basic_step_on_ended_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifier));
        sagaExecutor.execute(new EndEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(new StepEvent(identifier));
    }

    @Test
    public void execute_end_step_on_ended_saga_instance_should_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifier = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifier));
        sagaExecutor.execute(new EndEvent(identifier));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No available saga instance for the specified identifier");

        // When
        sagaExecutor.execute(new EndEvent(identifier));
    }

    @Test
    public void execute_multiple_end_step_on_available_saga_instance_should_terminate_them() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifierSaga1 = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifierSaga1));
        UUID identifierSaga2 = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifierSaga2));

        // When
        sagaExecutor.execute(new EndEvent(identifierSaga1));
        sagaExecutor.execute(new EndEvent(identifierSaga2));

        // Then
        assertFalse(sagaRepository.load(identifierSaga1).isPresent());
        assertFalse(sagaRepository.load(identifierSaga2).isPresent());
    }

    @Test
    public void execute_step_will_throw_exception() {
        // Given
        SagaExecutor sagaExecutor = sagaManager.register(new TestSaga(commandGateway));
        UUID identifierSaga = UUID.randomUUID();
        sagaExecutor.execute(new StartEvent(identifierSaga));

        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("Unexpected error in invoking step");

        // When
        sagaExecutor.execute(new ThrowExceptionEvent(identifierSaga));
    }

    @XKasperSaga(domain = TestDomain.class)
    public static class TestSaga implements Saga {

        private final KasperCommandGateway commandGateway;
        private int count;

        public TestSaga(KasperCommandGateway commandGateway) {
            this.commandGateway = commandGateway;
        }

        @XKasperSaga.Start(getter = "getId")
        public void start(StartEvent event){
            System.err.println("Saga is started !");
        }

        @XKasperSaga.Step(getter = "getId")
        public void step(StepEvent event){
            System.err.println("A step is invoked !");
            count++;
        }

        @XKasperSaga.Step(getter = "getId")
        public void throwException(ThrowExceptionEvent event){
            throw new RuntimeException("An exception is intended !");
        }

        @XKasperSaga.End(getter = "getId")
        public void end(EndEvent event){
            System.err.println("Saga is ended !");
        }

        @Override
        public Optional<SagaFactory> getFactory() {
            return Optional.absent();
        }

        public int getCount() {
            return count;
        }
    }

    public static class StartEvent extends AbstractEvent {
        public StartEvent(UUID id) {
            super(id);
        }
    }

    public static class StepEvent extends AbstractEvent {
        public StepEvent(UUID id) {
            super(id);
        }
    }

    public static class EndEvent extends AbstractEvent {
        public EndEvent(UUID id) {
            super(id);
        }
    }

    public static class ThrowExceptionEvent extends AbstractEvent {
        public ThrowExceptionEvent(UUID id) {
            super(id);
        }
    }

    private static class AbstractEvent implements Event {

        private final UUID id;

        public AbstractEvent(UUID id) {
            this.id = id;
        }

        public UUID getId(){
            return id;
        }
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public KasperCommandGateway commandGateway() {
            return mock(KasperCommandGateway.class);
        }
    }

}
