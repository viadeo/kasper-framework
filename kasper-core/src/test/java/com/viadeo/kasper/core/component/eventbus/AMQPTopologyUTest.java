package com.viadeo.kasper.core.component.eventbus;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.EventListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AMQPTopologyUTest {

    private AMQPTopology topology;

    @Captor
    private ArgumentCaptor<Binding> bindingArgumentCaptor;

    @Mock
    private RabbitAdmin rabbitAdmin;

    @Mock
    private ReflectionRoutingKeysResolver reflectionRoutingKeysResolver;

    @Before
    public void setUp() throws Exception {
        topology = new AMQPTopology(rabbitAdmin, reflectionRoutingKeysResolver, new AMQPComponentNameFormatter());
        when(reflectionRoutingKeysResolver.resolve(any(EventListener.class))).thenReturn(Arrays.asList(FakeEvent.class.getName()));
    }

    @Test
    public void createQueue_withDeprecatedEventListener_unbindQueue() throws Exception {
        // Given
        EventListener eventListener = new DeprecatedEventListener();

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        Assert.assertNotNull(queue);
        verify(rabbitAdmin, times(0)).declareBinding(any(Binding.class));
        verify(rabbitAdmin).removeBinding(bindingArgumentCaptor.capture());

        List<Binding> removedBindings = bindingArgumentCaptor.getAllValues();
        Assert.assertNotNull(removedBindings);
        Assert.assertEquals(1, removedBindings.size());
        Assert.assertEquals(FakeEvent.class.getName(), removedBindings.get(0).getRoutingKey());
    }

    @Test
    public void createQueue_withDeprecatedEventListener_withUnexpectedException_catchIt() throws Exception {
        // Given
        EventListener eventListener = new DeprecatedEventListener();
        doThrow(new RuntimeException("Fake event")).when(rabbitAdmin).removeBinding(any(Binding.class));

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        Assert.assertNotNull(queue);
        verify(rabbitAdmin, times(0)).declareBinding(any(Binding.class));
    }

    @Test
    public void createQueue_withEventListener_bindQueue() throws Exception {
        // Given
        EventListener eventListener = new NormalEventListener();

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        Assert.assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin, times(0)).removeBinding(any(Binding.class));

        List<Binding> removedBindings = bindingArgumentCaptor.getAllValues();
        Assert.assertNotNull(removedBindings);
        Assert.assertEquals(1, removedBindings.size());
        Assert.assertEquals(FakeEvent.class.getName(), removedBindings.get(0).getRoutingKey());
    }

    static class FakeEvent implements Event { }

    @Deprecated
    static class DeprecatedEventListener extends EventListener<FakeEvent> {
        @Override
        public EventResponse handle(Context context, FakeEvent event) {
            return EventResponse.success();
        }
    }

    static class NormalEventListener extends EventListener<FakeEvent> {
        @Override
        public EventResponse handle(Context context, FakeEvent event) {
            return EventResponse.success();
        }
    }
}
