// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class ProducerFactory {

    private final KafkaTerminalConfiguration.ProducerConfiguration configuration;

    public ProducerFactory(final KafkaTerminalConfiguration.ProducerConfiguration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration);
    }

    public <K, V> Producer<K, V> create() {
        final Properties properties = configuration.toProperties();
        properties.put("serializer.class", "com.viadeo.kasper.client.platform.components.eventbus.kafka.EventMessageSerializer");
        properties.put("key.serializer.class", "kafka.serializer.StringEncoder");

        return new Producer<>(new ProducerConfig(properties));
    }
}
