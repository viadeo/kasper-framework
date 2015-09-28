package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import org.axonframework.eventhandling.DefaultClusterMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AMQPClusterUTest {

    @Mock
    RabbitTemplate template;

    @Mock
    MessageListenerContainerManager containerManager;

    @Mock
    DefaultClusterMetaData clusterMetaData;

    @Mock
    AMQPTopology topology;

    @Mock
    MetricRegistry metricRegistry;

    @Mock
    QueueFinder queueFinder;

    ExchangeDescriptor exchangeDescriptor;
    private AMQPCluster cluster;


    @Before
    public void setUp() throws Exception {
        this.exchangeDescriptor = new ExchangeDescriptor("platform-test", "1");
        this.cluster = new AMQPCluster("default", exchangeDescriptor, topology, template, containerManager, metricRegistry, queueFinder);
    }

    @Test
    public void start_withObsoleteQueue_retrieveQueryBindingsFromVersionedExchange() throws Exception {
        // Given
        when(topology.createExchanges(exchangeDescriptor.name, exchangeDescriptor.version)).thenReturn(new TopicExchange("platform-test-1"));
        QueueInfo queueInfo = new QueueInfo("platform-test-1_default_theObsoleteQueueName", "platform-test-1", "theObsoleteQueueName", false);
        when(queueFinder.getObsoleteQueueNames()).thenReturn(Lists.newArrayList(queueInfo));

        // When
        cluster.start();

        // Then
        Mockito.verify(queueFinder).getQueueBindings(queueInfo);
    }
}
