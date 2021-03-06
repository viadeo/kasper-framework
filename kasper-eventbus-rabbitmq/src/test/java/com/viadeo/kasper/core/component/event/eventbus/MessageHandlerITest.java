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

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQEventBusConfiguration;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.spring.core.KasperConfiguration;
import com.viadeo.kasper.spring.core.KasperContextConfiguration;
import com.viadeo.kasper.spring.core.KasperIDConfiguration;
import com.viadeo.kasper.spring.core.KasperObjectMapperConfiguration;
import org.axonframework.domain.GenericEventMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.viadeo.kasper.core.component.event.eventbus.AMQPTopologyITest.TestEvent;
import static com.viadeo.kasper.core.component.event.eventbus.AMQPTopologyITest.TestEventListener;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KasperConfiguration.class,
        KasperContextConfiguration.class,
        KasperIDConfiguration.class,
        KasperObjectMapperConfiguration.class,
        MetricRegistry.class,
        RabbitMQConfiguration.class,
        RabbitMQEventBusConfiguration.AmqpClusterConfiguration.class
})
@ActiveProfiles(profiles = "rabbitmq")
public class MessageHandlerITest {

    public static final int TIMEOUT = 500;

    private static TestEventListener eventListenerWrapper;
    private static final EventListener<TestEvent> mockedEventListener = mock(EventListener.class);

    @Inject
    private AMQPCluster cluster;

    @Inject
    private MessageConverter messageConverter;

    @Inject
    private RabbitTemplate rabbitTemplate;

    @Inject
    private AMQPComponentNameFormatter nameFormatter;

    @Inject
    private Config config;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void init() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        eventListenerWrapper = new TestEventListener(mockedEventListener);
    }

    @Before
    public void setUp() throws Exception {
        reset((EventListener)mockedEventListener);
        cluster.subscribe(eventListenerWrapper);
        cluster.start();
    }

    @After
    public void tearDown() throws Exception {
        cluster.unsubscribe(eventListenerWrapper);
    }

    @Test
    public void handle_withoutTrouble_isOk() throws Exception {
        // Given
        when(mockedEventListener.handle(any(EventMessage.class))).thenReturn(EventResponse.success());
        ArgumentCaptor<EventMessage> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        TestEvent testEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener).handle(eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(testEvent, eventCaptor.getValue().getEvent());
    }

    @Test
    public void handle_withUnexpectedException_shouldRetry5TimesBeforeRequeueInDeadLetter() throws Exception {
        // Given
        doThrow(new RuntimeException("Bazinga!!")).when(mockedEventListener).handle(any(EventMessage.class));

        ArgumentCaptor<EventMessage> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);

        TestEvent testEvent = new TestEvent();

        // When
        cluster.doPublish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (EventMessage message : eventCaptor.getAllValues()) {
            assertEquals(testEvent, message.getEvent());
        }

        String clusterName = config.getString("runtime.eventbus.amqp.clusterName");
        String exchangeName = nameFormatter.getFullExchangeName(cluster.getExchangeDescriptor().name, cluster.getExchangeDescriptor().version);
        Message message = rabbitTemplate.receive(nameFormatter.getDeadLetterQueueName(exchangeName, clusterName, eventListenerWrapper));

        assertNotNull(message);
        assertEquals(testEvent, ((GenericEventMessage) messageConverter.fromMessage(message)).getPayload());
    }

}
