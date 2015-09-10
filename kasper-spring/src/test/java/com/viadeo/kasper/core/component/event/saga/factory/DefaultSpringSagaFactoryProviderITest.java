// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.factory;

import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class DefaultSpringSagaFactoryProviderITest {

    private DefaultSpringSagaFactoryProvider sagaFactoryProvider;

    @Before
    public void setUp() throws Exception {
        DefaultSpringSagaFactoryProvider.clearCache();

        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.refresh();

        sagaFactoryProvider = new DefaultSpringSagaFactoryProvider(applicationContext);
    }

    @Test
    public void getOrCreate_a_saga_with_constructor_declaring_parameters() {
        // Given
        KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);

        // When
        SagaFactory sagaFactory = sagaFactoryProvider.getOrCreate(new TestFixture.TestSagaB(commandGateway));

        // Then
        assertNotNull(sagaFactory);

        TestFixture.TestSagaB instance = sagaFactory.create("toto", TestFixture.TestSagaB.class);
        assertNotNull(instance);
        assertEquals(commandGateway, instance.getCommandGateway());
    }

    @Test
    public void getOrCreate_a_saga_without_default_constructor() {
        // When
        SagaFactory sagaFactory = sagaFactoryProvider.getOrCreate(new TestFixture.TestSagaA());

        // Then
        assertNotNull(sagaFactory);

        TestFixture.TestSagaA instance = sagaFactory.create("toto", TestFixture.TestSagaA.class);
        assertNotNull(instance);
    }

    @Test
    public void getOrCreate_a_saga_with_constructor_declaring_parameter_available_in_the_parent_context() {
        // Given
        KasperCommandGateway commandGateway = mock(KasperCommandGateway.class);

        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.getBeanFactory().registerSingleton("commandGateway", commandGateway);
        applicationContext.refresh();

        sagaFactoryProvider = new DefaultSpringSagaFactoryProvider(applicationContext);

        // When
        SagaFactory sagaFactory = sagaFactoryProvider.getOrCreate(new TestFixture.TestSagaB(commandGateway));

        // Then
        assertNotNull(sagaFactory);

        TestFixture.TestSagaB instance = sagaFactory.create("toto", TestFixture.TestSagaB.class);
        assertNotNull(instance);
        assertEquals(commandGateway, instance.getCommandGateway());
    }
}
