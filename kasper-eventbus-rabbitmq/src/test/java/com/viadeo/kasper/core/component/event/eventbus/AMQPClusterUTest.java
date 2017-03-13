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
import com.google.common.collect.Lists;
import org.axonframework.eventhandling.DefaultClusterMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
