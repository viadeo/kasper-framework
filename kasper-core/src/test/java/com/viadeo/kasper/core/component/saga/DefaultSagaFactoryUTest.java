// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.gateway.CommandGateway;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.saga.factory.DefaultSagaFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.support.GenericApplicationContext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class DefaultSagaFactoryUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    public GenericApplicationContext applicationContext;
    public DefaultSagaFactory factory;

    @Before
    public void setUp() throws Exception {
        applicationContext = new GenericApplicationContext();
        factory = new DefaultSagaFactory(applicationContext);
    }

    @Test
    public void create_withoutConstructor_isOk() {
        // When
        SagaWithoutConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithoutConstructor.class);

        // Then
        assertNotNull(instance);
    }

    @Test
    public void create_withoutParametersInConstructor_isOk() {
        // When
        SagaWithConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithConstructor.class);

        // Then
        assertNotNull(instance);
    }

    @Test
    public void create_withParametersInConstructor_isOk() {
        // Given
        CommandGateway commandGateway = mock(CommandGateway.class);
        applicationContext.getBeanFactory().registerSingleton("commandGateway", commandGateway);

        // When
        SagaWithParameterInConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithParameterInConstructor.class);

        // Then
        assertNotNull(instance);
        assertEquals(commandGateway, instance.getCommandGateway());
    }

    @Test
    public void create_withParametersInConstructor_withNoCandidates_isOk() {
        // Then
        expectedException.expect(SagaInstantiationException.class);
        expectedException.expectMessage("Error instantiating saga of 'com.viadeo.kasper.core.component.saga.DefaultSagaFactoryUTest$SagaWithParameterInConstructor'");

        // When
        factory.create(UUID.randomUUID().toString(), SagaWithParameterInConstructor.class);
    }

    class TestDomain implements Domain {}

    static abstract class AbstractSaga implements Saga {
        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }
    }

    @XKasperSaga(domain = TestDomain.class)
    public static class SagaWithoutConstructor extends AbstractSaga {}

    @XKasperSaga(domain = TestDomain.class)
    public static class SagaWithConstructor extends AbstractSaga {
        public SagaWithConstructor() {}
    }

    @XKasperSaga(domain = TestDomain.class)
    static class SagaWithParameterInConstructor extends AbstractSaga {
        private final CommandGateway commandGateway;

        public SagaWithParameterInConstructor(final CommandGateway commandGateway) {
            this.commandGateway = commandGateway;
        }

        public CommandGateway getCommandGateway() {
            return commandGateway;
        }
    }
}
