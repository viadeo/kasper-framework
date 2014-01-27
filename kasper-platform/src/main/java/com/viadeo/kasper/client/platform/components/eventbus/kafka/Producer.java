// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;

import java.util.Properties;

import static java.lang.Thread.sleep;

public class Producer {

    private final kafka.javaapi.producer.Producer<String, EventMessage> producer;
    private final String topic;

    public Producer(final Properties properties, final String topic) {
        this.topic = topic;
        this.producer = new kafka.javaapi.producer.Producer<>(new ProducerConfig(properties));
    }

    public void publish(final EventMessage message) {
        producer.send(new KeyedMessage<>(topic, message.getIdentifier(), message));
    }

    public static void main(String[] args) throws InterruptedException {
        final Properties props = new Properties();
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("metadata.broker.list", "localhost:9092");

        final Producer producer = new Producer(props, "sampleTopic");

        int messageNo = 1;

        while (true) {
            String messageStr = "Message_" + messageNo++;
            producer.publish((GenericEventMessage) GenericEventMessage.asEventMessage(messageStr));
            System.err.println("publish >> '" + messageStr + "'");
            sleep(5000L);
        }
    }
}
