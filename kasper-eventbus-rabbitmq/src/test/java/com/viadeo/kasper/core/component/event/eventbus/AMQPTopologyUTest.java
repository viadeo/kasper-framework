// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        assertNotNull(queue);
        verify(rabbitAdmin, times(0)).declareBinding(any(Binding.class));
        verify(rabbitAdmin).removeBinding(bindingArgumentCaptor.capture());

        List<Binding> removedBindings = bindingArgumentCaptor.getAllValues();
        assertNotNull(removedBindings);
        assertEquals(1, removedBindings.size());
        assertEquals(FakeEvent.class.getName(), removedBindings.get(0).getRoutingKey());
    }

    @Test
    public void createQueue_withDeprecatedEventListener_withUnexpectedException_catchIt() throws Exception {
        // Given
        AutowiredEventListener eventListener = new DeprecatedEventListener();
        doThrow(new RuntimeException("Fake event")).when(rabbitAdmin).removeBinding(any(Binding.class));

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        assertNotNull(queue);
        verify(rabbitAdmin, times(0)).declareBinding(any(Binding.class));
    }

    @Test
    public void createQueue_withEventListener_bindQueue() throws Exception {
        // Given
        AutowiredEventListener eventListener = new NormalEventListener();

        // When
        Queue queue = topology.createQueue("exchange", "1", "default", eventListener);

        // Then
        assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin, times(0)).removeBinding(any(Binding.class));

        List<Binding> removedBindings = bindingArgumentCaptor.getAllValues();
        assertNotNull(removedBindings);
        assertEquals(1, removedBindings.size());
        assertEquals(FakeEvent.class.getName(), removedBindings.get(0).getRoutingKey());
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
        assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin).removeBinding(argumentCaptor.capture());

        List<Binding> declaredBindings = bindingArgumentCaptor.getAllValues();
        assertNotNull(declaredBindings);
        assertEquals(1, declaredBindings.size());
        assertEquals(FakeEvent.class.getName(), declaredBindings.get(0).getRoutingKey());

        List<Binding> removedBindings = argumentCaptor.getAllValues();
        assertNotNull(removedBindings);
        assertEquals(1, removedBindings.size());
        assertEquals(FakeEvent2.class.getName(), removedBindings.get(0).getRoutingKey());
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
        assertNotNull(queue);
        verify(rabbitAdmin).declareBinding(bindingArgumentCaptor.capture());
        verify(rabbitAdmin).removeBinding(argumentCaptor.capture());

        List<Binding> declaredBindings = bindingArgumentCaptor.getAllValues();
        assertNotNull(declaredBindings);
        assertEquals(1, declaredBindings.size());
        assertEquals(FakeEvent.class.getName(), declaredBindings.get(0).getRoutingKey());

        List<Binding> removedBindings = argumentCaptor.getAllValues();
        assertNotNull(removedBindings);
        assertEquals(1, removedBindings.size());

        Binding actualBinding = removedBindings.get(0);
        assertEquals(obsoleteBinding.getExchange(), actualBinding.getExchange());
        assertEquals(obsoleteBinding.getRoutingKey(), actualBinding.getRoutingKey());
        assertEquals(obsoleteBinding.getDestination(), actualBinding.getDestination());
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
