package org.axonframework.eventhandling.amqp;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConversionException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MessageListener implementation that deserializes incoming messages and forwards them to one or more clusters. The
 * <code>byte[]</code> making up the message payload must the format as used by the {@link SpringAMQPTerminal}.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class ClusterMessageListener extends SimpleMessageListenerContainer implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ClusterMessageListener.class);

    private final List<Cluster> clusters = new CopyOnWriteArrayList<Cluster>();
    private final AMQPMessageConverter messageConverter;

    /**
     * Initializes a ClusterMessageListener with given <code>initialCluster</code> that uses given
     * <code>serializer</code> to deserialize the message's contents into an EventMessage.
     *
     * @param initialCluster   The first cluster to assign to the listener
     * @param messageConverter The message converter to use to convert AMQP Messages to Event Messages
     */
    public ClusterMessageListener(Cluster initialCluster, AMQPMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
        this.clusters.add(initialCluster);
    }

    @Override
    public void onMessage(Message message) {
        try {
            EventMessage eventMessage = messageConverter.readAMQPMessage(message.getBody(),
                    message.getMessageProperties());
            if (eventMessage != null) {
                for (Cluster cluster : clusters) {
                    cluster.publish(eventMessage);
                }
            }
        } catch (Exception e) {
            throw new MessageConversionException("Unable to deserialize an incoming message", e);
        }
    }

    /**
     * Registers an additional cluster. This cluster will receive messages once registered.
     *
     * @param cluster the cluster to add to the listener
     */
    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }


}
