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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.rabbitmq.client.Channel;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterMetaData;
import org.axonframework.eventhandling.DefaultClusterMetaData;
import org.axonframework.eventhandling.EventProcessingMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.SmartLifecycle;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class AMQPCluster implements Cluster, SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPCluster.class);

    private final RabbitTemplate template;
    private final String name;
    private final MessageListenerContainerManager containerManager;
    private final DefaultClusterMetaData clusterMetaData;
    private final AMQPTopology topology;
    private final ExchangeDescriptor exchangeDescriptor;
    private final MetricRegistry metricRegistry;

    private String exchangeName;
    private QueueFinder queueFinder;

    public AMQPCluster(
            String name,
            ExchangeDescriptor exchangeDescriptor,
            AMQPTopology topology,
            RabbitTemplate template,
            MessageListenerContainerManager containerManager,
            MetricRegistry metricRegistry,
            QueueFinder queueFinder
    ) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.exchangeDescriptor = checkNotNull(exchangeDescriptor);
        this.topology = checkNotNull(topology);
        this.name = checkNotNull(name);
        this.template = checkNotNull(template);
        this.containerManager = checkNotNull(containerManager);
        this.clusterMetaData = new DefaultClusterMetaData();
        this.queueFinder = queueFinder;
    }

    @Override
    public void subscribe(final org.axonframework.eventhandling.EventListener eventListener) {
        checkNotNull(eventListener);
        if (EventListener.class.isAssignableFrom(eventListener.getClass())) {
            this.subscribe((EventListener) eventListener);
        }
    }

    /**
     * Setup and start and associate a spring amqp container to the given listener
     * This method only accept instances of kasper events because we need to get
     * the managed event class in order to process the topology.
     *
     * @param eventListener eventListener to subscribe
     */
    public void subscribe(final EventListener eventListener) {
        try {
            LOGGER.debug("subscribing event listener {} to amqp", eventListener.getClass().getName());
            final Queue queue = topology.createQueue(exchangeDescriptor.name, exchangeDescriptor.version, getName(), eventListener);
            topology.createDeadLetterQueue(exchangeDescriptor.name, exchangeDescriptor.version, getName(), eventListener);

            containerManager.register(eventListener, queue.getName());
        } catch (Throwable e) {
            LOGGER.error("unable to subscribe event listener {} to amqp", eventListener.getClass().getName(), e);
        }
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Publish messages using provided rabbit template
     *
     * @param events a list of message to publish
     * @see org.springframework.amqp.rabbit.core.RabbitTemplate#execute(org.springframework.amqp.rabbit.core.ChannelCallback)
     */
    @Override
    public void publish(final EventMessage... events) {
        try {
            doPublish(events);
        } catch (Exception e) {
            LOGGER.error("unable to publish to amqp", e);
        }
    }

    @VisibleForTesting
    protected void doPublish(final EventMessage... events) {
        template.execute(new ChannelCallback<Object>() {
            @Override
            public Object doInRabbit(Channel channel) throws Exception {
                for (EventMessage event : events) {
                    try {
                        template.convertAndSend(exchangeName, event.getPayloadType().getName(), event);
                        metricRegistry.meter(name(getClass(), getName(), "published")).mark();
                    } catch (Exception e) {
                        metricRegistry.meter(name(getClass(), getName(), "failed")).mark();
                        LOGGER.error("Failed to publish the event to amqp, <payload={}> <payloadType={}> <metadata={}>",
                                event.getPayload(), event.getPayloadType(), event.getMetaData(), e);
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void unsubscribe(final org.axonframework.eventhandling.EventListener eventListener) {
        checkNotNull(eventListener);
        if (EventListener.class.isAssignableFrom(eventListener.getClass())) {
            this.unsubscribe((EventListener) eventListener);
        }
    }

    /**
     * Stop the associated container and remove it from the list of containers
     *
     * @param eventListener listener to unsubscribe
     * @see org.axonframework.eventhandling.Cluster#unsubscribe(org.axonframework.eventhandling.EventListener)
     */
    public void unsubscribe(EventListener eventListener) {
        LOGGER.debug("unsubscribing event listener {} to amqp", eventListener.getName());
        containerManager.unregister(eventListener);
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getMembers()
     */
    @Override
    public Set<org.axonframework.eventhandling.EventListener> getMembers() {
        return Sets.newHashSet(
                Iterables.transform(containerManager.containers(), new Function<MessageListenerContainer, org.axonframework.eventhandling.EventListener>() {
                    @Override
                    public org.axonframework.eventhandling.EventListener apply(MessageListenerContainer input) {
                        return input.getEventListener();
                    }
                })
        );
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getMetaData()
     */
    @Override
    public ClusterMetaData getMetaData() {
        return clusterMetaData;
    }

    /**
     * @return the object allowing to create the amqp topology
     */
    public AMQPTopology getTopology() {
        return topology;
    }

    @Override
    public synchronized boolean isRunning() {
        for (SimpleMessageListenerContainer container : containerManager.containers()) {
            if (container.isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        long start = System.currentTimeMillis();

        try {
            exchangeName = exchangeDescriptor.name;
            topology.createExchanges(exchangeDescriptor.name, exchangeDescriptor.version).getName();
            topology.createFallbackDeadLetterQueue(exchangeDescriptor.name, exchangeDescriptor.version, getName());
        } catch (AmqpException e) {
            throw new RuntimeException("Failed to initialize AMQP topology", e);
        }

        try {
            for (QueueInfo queueInfo : queueFinder.getObsoleteQueueNames()) {
                LOGGER.info("Detected obsolete queue : {}", queueInfo.getQueueName());

                for (Binding binding : queueFinder.getQueueBindings(queueInfo)) {
                    topology.unbindQueue(binding);
                    LOGGER.info("Unbind obsolete queue : {}", binding);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Unexpected error when unbind obsolete queues", t);
        }

        containerManager.startAll();
        LOGGER.info("Event bus started in {} ms", System.currentTimeMillis() - start);
    }

    @Override
    public void stop() {
        containerManager.stopAll();
    }

    @Override
    public void subscribeEventProcessingMonitor(EventProcessingMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsubscribeEventProcessingMonitor(EventProcessingMonitor monitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 1;
    }

    public ExchangeDescriptor getExchangeDescriptor() {
        return exchangeDescriptor;
    }

}