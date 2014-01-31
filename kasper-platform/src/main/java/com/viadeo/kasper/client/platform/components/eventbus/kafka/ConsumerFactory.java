// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.DefaultDecoder;
import org.axonframework.domain.EventMessage;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ConsumerFactory {

    private final KafkaTerminalConfiguration.ConsumerConfiguration configuration;
    private final DefaultDecoder keyDecoder;
    private final EventMessageSerializer valueDecoder;

    public ConsumerFactory(final KafkaTerminalConfiguration.ConsumerConfiguration configuration) {
        this.configuration = checkNotNull(configuration);
        this.keyDecoder = new DefaultDecoder(null);
        this.valueDecoder = new EventMessageSerializer();
    }

    public ConsumerConnector createConnector(final String category) {
        checkNotNull(category);
        final Properties properties = getPropertiesFor(category);
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    public List<KafkaStream<byte[], EventMessage>> createStreams(final int numStreams, final ConsumerConnector consumer){
        checkNotNull(consumer);
        checkState(numStreams > 0);
        return consumer.createMessageStreamsByFilter(
                new Whitelist(".*"),
                numStreams,
                keyDecoder,
                valueDecoder
        );
    }

    public Map<String, List<KafkaStream<byte[], EventMessage>>> createStreams(final int nbOfThreadPerStreams, final ConsumerConnector consumer, final String... topics){
        checkNotNull(consumer);
        checkNotNull(topics);
        checkState(nbOfThreadPerStreams > 0);
        checkState(topics.length > 0);

        final Map<String, Integer> topicCountMap = Maps.newHashMap();

        for(final String topic:topics){
            topicCountMap.put(topic, nbOfThreadPerStreams);
        }

        return consumer.createMessageStreams(
                topicCountMap,
                keyDecoder,
                valueDecoder
        );
    }

    protected Properties getPropertiesFor(final String category) {
        final Properties properties = configuration.toProperties();
        properties.put("group.id", checkNotNull(category));
        return properties;
    }
}
