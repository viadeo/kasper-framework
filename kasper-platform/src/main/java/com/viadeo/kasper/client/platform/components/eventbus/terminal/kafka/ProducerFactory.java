// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProducerFactory {

    private final Properties baseProperties;

    public ProducerFactory(final ProducerConfig producerConfig) {
        this.baseProperties = checkNotNull(producerConfig).props().props();
    }

    public <K, V> Producer<K, V> create() {
        final Properties properties = getProperties();
        return new Producer<>(new ProducerConfig(properties));
    }

    protected Properties getProperties() {
        final Properties properties = new Properties();

        for (final Object key : baseProperties.keySet()) {
            properties.put(key, baseProperties.get(key));
        }

        properties.put("serializer.class", EventMessageSerializer.class.getName());
        properties.put("key.serializer.class", StringEncoder.class.getName());
        return properties;
    }
}
