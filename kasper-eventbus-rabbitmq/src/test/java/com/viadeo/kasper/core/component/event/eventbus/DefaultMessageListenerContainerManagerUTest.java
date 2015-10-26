// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaExecutor;
import com.viadeo.kasper.core.component.event.saga.SagaWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.support.converter.MessageConverter;

import static com.viadeo.kasper.core.component.event.eventbus.MessageListenerContainerUTest.MessageListener;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageListenerContainerManagerUTest {

    @Mock
    MessageListenerContainerFactory messageListenerContainerFactory;

    @Mock
    MetricRegistry metricRegistry;

    @Mock
    MessageConverter messageConverter;

    @Mock
    MessageListenerContainerController messageListenerContainerController;

    @InjectMocks
    DefaultMessageListenerContainerManager containerManager;


    @Test
    public void register_twice_a_saga_manager_is_ok() {
        when(messageListenerContainerFactory.create(any(String.class), any(EventListener.class), any(MessageListener.class))).thenReturn(mock(MessageListenerContainer.class));

        SagaExecutor sagaExecutorA = mock(SagaExecutor.class);
        when(sagaExecutorA.getSagaClass()).thenReturn(TestSaga.class);

        SagaExecutor sagaExecutorB = mock(SagaExecutor.class);
        when(sagaExecutorB.getSagaClass()).thenReturn(Saga.class);

        containerManager.register(new SagaWrapper(sagaExecutorA), "sagaA");
        containerManager.register(new SagaWrapper(sagaExecutorB), "sagaB");

        assertTrue(containerManager.get(TestSaga.class).isPresent());
        assertTrue(containerManager.get(Saga.class).isPresent());
    }

    private interface TestSaga extends Saga { }
}
