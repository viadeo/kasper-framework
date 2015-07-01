// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.event.saga.repository.SagaRepository;
import com.viadeo.kasper.event.saga.step.StepProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.viadeo.kasper.event.saga.TestFixture.TestSagaA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class SagaManagerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SagaFactory sagaFactory;
    private SagaRepository sagaRepository;
    private SagaManager sagaManager;

    @Before
    public void setUp() throws Exception {
        sagaFactory = mock(SagaFactory.class);
        sagaRepository = mock(SagaRepository.class);
        sagaManager = new SagaManager(sagaFactory, sagaRepository, mock(StepProcessor.class));
    }

    @Test
    public void register_withSaga_returnRelatedExecutor() {
        // When
        SagaExecutor sagaExecutor = sagaManager.register(new TestSagaA());

        // Then
        assertNotNull(sagaExecutor);
        assertEquals(sagaFactory, sagaExecutor.getSagaFactory());
        assertEquals(sagaRepository, sagaExecutor.getSagaRepository());
        assertEquals(TestSagaA.class, sagaExecutor.getSagaClass());
    }

    @Test
    public void register_withAlreadyRegisteredSaga_isKO() {
        //Given
        sagaManager.register(new TestSagaA());

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("The specified saga is already registered");

        // When
        sagaManager.register(new TestSagaA());
    }
}
