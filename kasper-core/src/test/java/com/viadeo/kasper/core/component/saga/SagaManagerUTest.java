// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga;

import com.viadeo.kasper.core.component.saga.SagaExecutor;
import com.viadeo.kasper.core.component.saga.SagaManager;
import com.viadeo.kasper.core.component.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.saga.step.StepProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.viadeo.kasper.core.component.saga.TestFixture.TestSagaA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SagaManagerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SagaRepository sagaRepository;
    private SagaManager sagaManager;
    private SagaFactoryProvider sagaFactoryProvider;

    @Before
    public void setUp() throws Exception {
        sagaFactoryProvider = mock(SagaFactoryProvider.class);
        sagaRepository = mock(SagaRepository.class);
        sagaManager = new SagaManager(sagaFactoryProvider, sagaRepository, mock(StepProcessor.class));
    }

    @Test
    public void register_withSaga_returnRelatedExecutor() {
        // Given
        TestSagaA saga = new TestSagaA();
        SagaFactory sagaFactory = mock(SagaFactory.class);
        when(sagaFactoryProvider.getOrCreate(saga)).thenReturn(sagaFactory);

        // When
        SagaExecutor sagaExecutor = sagaManager.register(saga);

        // Then
        assertNotNull(sagaExecutor);
        assertEquals(sagaFactory, sagaExecutor.getSagaFactory());
        assertEquals(sagaRepository, sagaExecutor.getSagaRepository());
        assertEquals(TestSagaA.class, sagaExecutor.getSagaClass());
    }

    @Test
    public void register_withAlreadyRegisteredSaga_isKO() {
        // Given
        TestSagaA saga = new TestSagaA();
        SagaFactory sagaFactory = mock(SagaFactory.class);
        when(sagaFactoryProvider.getOrCreate(saga)).thenReturn(sagaFactory);
        sagaManager.register(saga);

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("The specified saga is already registered");

        // When
        sagaManager.register(saga);
    }
}
