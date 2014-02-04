// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka;

import com.viadeo.kasper.client.platform.components.eventbus.EventBusTerminalFactory;
import kafka.consumer.ConsumerConfig;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class KafkaTerminalFactory implements EventBusTerminalFactory {

    private final ConsumerConfig consumerConfig;
    private final ProducerConfig producerConfig;

    public KafkaTerminalFactory() {
        this(new Properties());
    }

    public KafkaTerminalFactory(final Properties properties) {
        this(new ConsumerConfig(properties), new ProducerConfig(properties));
    }

    public KafkaTerminalFactory(final ConsumerConfig consumerConfig, final ProducerConfig producerConfig) {
        this.consumerConfig = consumerConfig;
        this.producerConfig = producerConfig;
    }

    @Override
    public KafkaTerminal createEventBusTerminal() {
        return new KafkaTerminal(new ConsumerFactory(consumerConfig), new ProducerFactory(producerConfig));
    }
}
