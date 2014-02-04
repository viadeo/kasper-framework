// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.EventB;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainB.DomainB;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventListener;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class KafkaTerminalUTest {

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void publish_withNullAsEventMessages_throwException() {
        // Given
        final ConsumerFactory consumerFactory = mock(ConsumerFactory.class);

        final ProducerFactory producerFactory = mock(ProducerFactory.class);
        when(producerFactory.create()).thenReturn(mock(Producer.class));

        final KafkaTerminal kafkaTerminal = new KafkaTerminal(consumerFactory, producerFactory);

        // When
        kafkaTerminal.publish(null);

        // Then throws an exception
    }

    @Test
    @SuppressWarnings("unchecked")
    public void publish_withSeveralEventMessages_isOk() {
        // Given
        final ConsumerFactory consumerFactory = mock(ConsumerFactory.class);

        final Producer<String, EventMessage> producer = mock(Producer.class);

        final ProducerFactory producerFactory = mock(ProducerFactory.class);
        when(producerFactory.<String, EventMessage>create()).thenReturn(producer);

        final KafkaTerminal kafkaTerminal = new KafkaTerminal(consumerFactory, producerFactory);

        final GenericEventMessage<String> firstMessage = new GenericEventMessage<>("1rst");
        final GenericEventMessage<String> secondMessage = new GenericEventMessage<>("2nd");

        // When
        kafkaTerminal.publish(firstMessage, secondMessage);

        // Then
        verify(producer).send(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void publish_withOneEventMessage_isOk() {
        // Given
        final ConsumerFactory consumerFactory = mock(ConsumerFactory.class);

        final Producer<String, EventMessage> producer = mock(Producer.class);

        final ProducerFactory producerFactory = mock(ProducerFactory.class);
        when(producerFactory.<String, EventMessage>create()).thenReturn(producer);

        final KafkaTerminal kafkaTerminal = new KafkaTerminal(consumerFactory, producerFactory);

        final EventMessage message = mock(EventMessage.class);
        when(message.getIdentifier()).thenReturn("1");
        when(message.getPayloadType()).thenReturn(String.class);


        // When
        kafkaTerminal.publish(message);

        // Then
        verify(producer).send(
                refEq(
                        Lists.newArrayList(
                                new KeyedMessage<>(String.class.getName(), "1", message)
                        )
                )
        );
    }

    @Test(expected = NullPointerException.class)
    public void onClusterCreated_withNullAsCluster_throwException() {
        // Given
        final KafkaTerminal terminal = new KafkaTerminal(mock(ConsumerFactory.class), mock(ProducerFactory.class));

        // When
        terminal.onClusterCreated(null);

        // Then throws an exception
    }

    @Test
    public void onClusterCreated_withOneEventListener_createOneCluster() {
        // Given
        final DomainA.EventListenerA eventListenerA = new DomainA.EventListenerA();
        final String category = "titi";

        final ConsumerConnector consumerConnector = mock(ConsumerConnector.class);

        final ConsumerFactory consumerFactory = mock(ConsumerFactory.class);
        when(consumerFactory.createConnector(category)).thenReturn(consumerConnector);

        final Cluster cluster = mock(Cluster.class);
        when(cluster.getName()).thenReturn(category);
        when(cluster.getMembers()).thenReturn(Sets.<EventListener>newHashSet(eventListenerA));

        final KafkaTerminal terminal = new KafkaTerminal(consumerFactory, mock(ProducerFactory.class));

        // When
        terminal.onClusterCreated(cluster);

        // Then
        assertEquals(1, terminal.getConsumers().size());
    }

    @Test
    public void onClusterCreated_withTwoEventListeners_createOneCluster() {
        // Given
        final DomainB.EventListenerA eventListenerA = new DomainB.EventListenerA();
        final DomainB.EventListenerB eventListenerB = new DomainB.EventListenerB();

        final String category = "titi";

        final ConsumerConnector consumerConnector = mock(ConsumerConnector.class);

        final ConsumerFactory consumerFactory = mock(ConsumerFactory.class);
        when(consumerFactory.createConnector(category)).thenReturn(consumerConnector);

        final Cluster cluster = mock(Cluster.class);
        when(cluster.getName()).thenReturn(category);
        when(cluster.getMembers()).thenReturn(Sets.<EventListener>newHashSet(eventListenerA, eventListenerB));

        final KafkaTerminal terminal = new KafkaTerminal(consumerFactory, mock(ProducerFactory.class));

        // When
        terminal.onClusterCreated(cluster);

        // Then
        assertEquals(1, terminal.getConsumers().size());
    }

    @Test
    public void normalize_withFullyQualifiedNameOfAClass_isOk() throws Exception {
        // Given
        final KafkaTerminal terminal = new KafkaTerminal(mock(ConsumerFactory.class), mock(ProducerFactory.class));

        // When
        final String actualTopic = terminal.normalize(EventB.class.getName());

        // Then
        assertNotNull(actualTopic);
        assertEquals("com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.EventB", actualTopic);
    }

    @Test
    public void normalize_withFullyQualifiedNameOfAnInnerClass_isOk() throws Exception {
        // Given
        final KafkaTerminal terminal = new KafkaTerminal(mock(ConsumerFactory.class), mock(ProducerFactory.class));

        // When
        final String actualTopic = terminal.normalize(DomainA.EventA.class.getName());

        // Then
        assertNotNull(actualTopic);
        assertEquals("com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA_EventA", actualTopic);
    }

    @Test
    public void normalize_withAccentedCharacters_isOk() throws Exception {
        // Given
        final KafkaTerminal terminal = new KafkaTerminal(mock(ConsumerFactory.class), mock(ProducerFactory.class));

        // When
        final String actualTopic = terminal.normalize("ĉoùcöu");

        // Then
        assertNotNull(actualTopic);
        assertEquals("coucou", actualTopic);
    }
}
