package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
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

import java.util.HashMap;
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
    private QueueFinder queueFinder;

    @Mock
    private ReflectionRoutingKeysResolver reflectionRoutingKeysResolver;

    @Before
    public void setUp() throws Exception {
        topology = new AMQPTopology(rabbitAdmin, reflectionRoutingKeysResolver, queueFinder, new AMQPComponentNameFormatter());
        when(reflectionRoutingKeysResolver.resolve(any(AutowiredEventListener.class))).thenReturn(
                new RoutingKeys(Sets.newHashSet(new RoutingKeys.RoutingKey(FakeEvent.class.getName())))
        );
    }

    @Test
    public void createQueue_withDeprecatedEventListener_unbindQueue() throws Exception {
        // Given
        AutowiredEventListener eventListener = new DeprecatedEventListener();

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
        AutowiredEventListener eventListener = new DeprecatedEventListener();
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
        AutowiredEventListener eventListener = new NormalEventListener();

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

    @Test
    public void createQueue_withMultiEventListener_bindQueue() throws Exception {
        // Given
        EventListener eventListener = new NormalEventListener();
        when(reflectionRoutingKeysResolver.resolve(any(AutowiredEventListener.class))).thenReturn(
                new RoutingKeys(Sets.newHashSet(
                        new RoutingKeys.RoutingKey(FakeEvent.class.getName()),
                        new RoutingKeys.RoutingKey(FakeEvent2.class.getName(), Boolean.TRUE)
                ))
        );
        ArgumentCaptor<Binding> argumentCaptor = ArgumentCaptor.forClass(Binding.class);

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        Assert.assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin).removeBinding(argumentCaptor.capture());

        List<Binding> declaredBindings = bindingArgumentCaptor.getAllValues();
        Assert.assertNotNull(declaredBindings);
        Assert.assertEquals(1, declaredBindings.size());
        Assert.assertEquals(FakeEvent.class.getName(), declaredBindings.get(0).getRoutingKey());

        List<Binding> removedBindings = argumentCaptor.getAllValues();
        Assert.assertNotNull(removedBindings);
        Assert.assertEquals(1, removedBindings.size());
        Assert.assertEquals(FakeEvent2.class.getName(), removedBindings.get(0).getRoutingKey());
    }

    @Test
    public void createQueue_withEventListener_withObsoleteBindings_removeObsoleteBinding() throws Exception {
        // Given
        EventListener eventListener = new NormalEventListener();
        ArgumentCaptor<Binding> argumentCaptor = ArgumentCaptor.forClass(Binding.class);
        Binding obsoleteBinding = new Binding("exchange-1_default_" + eventListener.getClass().getName(), Binding.DestinationType.QUEUE, "exchange-1", "test", new HashMap<String, Object>());
        Binding goodBinding = new Binding("exchange-1_default_" + eventListener.getClass().getName(), Binding.DestinationType.QUEUE, "exchange-1", FakeEvent.class.getName(), new HashMap<String, Object>());

        when(queueFinder.getQueueBindings(anyString())).thenReturn(Lists.newArrayList(obsoleteBinding, goodBinding));

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        Assert.assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin).removeBinding(argumentCaptor.capture());

        List<Binding> declaredBindings = bindingArgumentCaptor.getAllValues();
        Assert.assertNotNull(declaredBindings);
        Assert.assertEquals(1, declaredBindings.size());
        Assert.assertEquals(FakeEvent.class.getName(), declaredBindings.get(0).getRoutingKey());

        List<Binding> removedBindings = argumentCaptor.getAllValues();
        Assert.assertNotNull(removedBindings);
        Assert.assertEquals(1, removedBindings.size());

        Binding actualBinding = removedBindings.get(0);
        Assert.assertEquals(obsoleteBinding.getExchange(), actualBinding.getExchange());
        Assert.assertEquals(obsoleteBinding.getRoutingKey(), actualBinding.getRoutingKey());
        Assert.assertEquals(obsoleteBinding.getDestination(), actualBinding.getDestination());
    }


    static class FakeEvent implements Event { }
    static class FakeEvent2 implements Event { }

    @Deprecated
    static class DeprecatedEventListener extends AutowiredEventListener<FakeEvent> {
        @Override
        public EventResponse handle(Context context, FakeEvent event) {
            return EventResponse.success();
        }
    }

    static class NormalEventListener extends AutowiredEventListener<FakeEvent> {
        @Override
        public EventResponse handle(Context context, FakeEvent event) {
            return EventResponse.success();
        }
    }
}
