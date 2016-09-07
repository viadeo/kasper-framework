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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import io.github.fallwizard.rabbitmq.mgmt.BindingOperations;
import io.github.fallwizard.rabbitmq.mgmt.QueueOperations;
import io.github.fallwizard.rabbitmq.mgmt.RabbitMgmtService;
import io.github.fallwizard.rabbitmq.mgmt.model.Binding;
import io.github.fallwizard.rabbitmq.mgmt.model.Queue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.StandardEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.amqp.core.Binding.DestinationType;

public class QueueFinderUTest {

    private StandardEnvironment environment;

    public static class Fake_EventListener extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    public static class FakeEventListener extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Profile("!test")
    public static class AnnotatedFakeEventListener extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    private String vhost;
    private String fullExchangeName;
    private QueueFinder queueFinder;
    private List<Queue> queues;
    private List<Binding> bindings;
    private AMQPComponentNameFormatter amqpComponentNameFormatter;

    @Before
    public void setUp() throws Exception {
        amqpComponentNameFormatter = new AMQPComponentNameFormatter();
        vhost = "/";
        fullExchangeName = amqpComponentNameFormatter.getFullExchangeName("exchangeName", "1");
        queues = Lists.newArrayList();
        bindings = Lists.newArrayList();

        QueueOperations queueOperations = mock(QueueOperations.class);
        when(queueOperations.allOnVHost(vhost)).thenReturn(Optional.<Collection<Queue>>of(queues));

        BindingOperations bindingOperations = mock(BindingOperations.class);
        when(bindingOperations.get(eq(vhost), anyString(), anyString(), eq(false))).thenReturn(Optional.<Collection<Binding>>of(bindings));

        RabbitMgmtService rabbitMgmtService = mock(RabbitMgmtService.class);
        when(rabbitMgmtService.queues()).thenReturn(queueOperations);
        when(rabbitMgmtService.bindings()).thenReturn(bindingOperations);

        environment = new StandardEnvironment();

        queueFinder = new QueueFinder(
                amqpComponentNameFormatter,
                rabbitMgmtService,
                vhost,
                environment,
                fullExchangeName,
                fullExchangeName + "_default_dead-letter"
        );
    }

    @Test
    public void getObsoleteQueueNames_withNoQueue_returnNothing() throws Exception {
        // Given no queue

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new FakeEventListener())));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneDeadLetterQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", FakeEventListener.class.getName() + "_dead-letter")));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_notMatchingQueueName_returnNothing() throws Exception {
        // Given
        queues.add(new Queue("miaou"));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneQueue_notRespectNamingConvention_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(fullExchangeName + "_clusterName_" + Fake_EventListener.class.getName()));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withOneObsoleteQueue_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", "miaou");
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals("miaou", queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withOneObsoleteDeadLetterQueue_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", "miaou_dead-letter");
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals("miaou", queueInfo.getEventListenerClassName());
        assertTrue(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withTestAsActiveProfile_withOneQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new FakeEventListener())));
        environment.setActiveProfiles("test");

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getObsoleteQueueNames_withTestAsActiveProfile_withOneAnnotatedQueue_withTestAsInactiveProfile_returnTheQueueName() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName(fullExchangeName, "clusterName", new AnnotatedFakeEventListener());
        queues.add(new Queue(queueName));
        environment.setActiveProfiles("test");

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals(fullExchangeName, queueInfo.getExchangeName());
        assertEquals(AnnotatedFakeEventListener.class.getName(), queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withQueueOfOutdatedTopology_returnNothing() throws Exception {
        // Given
        String queueName = amqpComponentNameFormatter.getQueueName("outdatedPlatform", "clusterName", new AnnotatedFakeEventListener());
        queues.add(new Queue(queueName));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(1, obsoleteQueueNames.size());

        QueueInfo queueInfo = obsoleteQueueNames.iterator().next();
        assertEquals(queueName, queueInfo.getQueueName());
        assertEquals("outdatedPlatform", queueInfo.getExchangeName());
        assertEquals(AnnotatedFakeEventListener.class.getName(), queueInfo.getEventListenerClassName());
        assertFalse(queueInfo.isDeadLetter());
    }

    @Test
    public void getObsoleteQueueNames_withFallbackDeadLetterQueue_returnNothing() throws Exception {
        // Given
        queues.add(new Queue(fullExchangeName + "_default_dead-letter"));

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
        assertEquals(0, obsoleteQueueNames.size());
    }

    @Test
    public void getQueueBindings_withNoBinding_returnNothing() {
        // Given

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(fullExchangeName + "_clusterName_queueName");

        // Then
        assertNotNull(queueBindings);
        assertTrue(queueBindings.isEmpty());
    }

    @Test
    public void getQueueBindings_withBinding_returnTheBinding() {
        // Given
        HashMap<String,Object> arguments = Maps.newHashMap();
        String queueName = "queueName";
        bindings.add(new Binding(fullExchangeName, vhost, queueName, "queue", "routing_key", "properties_key", arguments));

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(
                new QueueInfo(
                        fullExchangeName + "_clusterName_" + queueName,
                        fullExchangeName,
                        queueName,
                        false
                )
        );

        // Then
        assertNotNull(queueBindings);
        assertEquals(1, queueBindings.size());

        org.springframework.amqp.core.Binding binding = queueBindings.iterator().next();
        assertEquals(fullExchangeName, binding.getExchange());
        assertEquals(queueName, binding.getDestination());
        assertEquals(DestinationType.QUEUE, binding.getDestinationType());
        assertEquals("routing_key", binding.getRoutingKey());
        assertEquals(arguments, binding.getArguments());
    }

    @Test
    public void getQueueBindings_withBindingRelatedToADeadLetter_returnTheBinding() {
        // Given
        HashMap<String,Object> arguments = Maps.newHashMap();
        String queueName = "queueName";
        bindings.add(new Binding(fullExchangeName + "_dead-letter", vhost, queueName, "queue", "routing_key", "properties_key", arguments));

        // When
        Collection<org.springframework.amqp.core.Binding> queueBindings = queueFinder.getQueueBindings(
                new QueueInfo(
                        fullExchangeName + "_clusterName_" + queueName + "_dead-letter",
                        fullExchangeName + "_dead-letter",
                        queueName,
                        true
                )
        );

        // Then
        assertNotNull(queueBindings);
        assertEquals(1, queueBindings.size());

        org.springframework.amqp.core.Binding binding = queueBindings.iterator().next();
        assertEquals(fullExchangeName + "_dead-letter", binding.getExchange());
        assertEquals(queueName, binding.getDestination());
        assertEquals(DestinationType.QUEUE, binding.getDestinationType());
        assertEquals("routing_key", binding.getRoutingKey());
        assertEquals(arguments, binding.getArguments());
    }
}
