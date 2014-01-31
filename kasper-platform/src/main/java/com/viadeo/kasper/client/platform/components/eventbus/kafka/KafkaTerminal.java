// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration.ConsumerConfiguration;
import static com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration.ProducerConfiguration;

public class KafkaTerminal implements EventBusTerminal {

    private final Map<String, ConsumerConnector> consumerByCategory;
    private final Producer<String,EventMessage> producer;
    private final ConsumerFactory consumerFactory;

    public KafkaTerminal(
            final ConsumerConfiguration consumerConfiguration,
            final ProducerConfiguration producerConfiguration
    ) {
        this(new ConsumerFactory(consumerConfiguration), new ProducerFactory(producerConfiguration));
    }

    public KafkaTerminal(
            final ConsumerFactory consumerFactory,
            final ProducerFactory producerFactory
    ) {
        this.consumerFactory = checkNotNull(consumerFactory);
        this.producer = checkNotNull(producerFactory).create();
        this.consumerByCategory = Maps.newHashMap();
    }

    @Override
    public void publish(final EventMessage... eventMessages) {
        checkNotNull(eventMessages);
        final List<KeyedMessage<String,EventMessage>> messages = Lists.newArrayList();

        for (final EventMessage eventMessage : eventMessages) {
            messages.add(
                    new KeyedMessage<>(
                            normalize(eventMessage.getPayloadType().getName()),
                            eventMessage.getIdentifier(),
                            eventMessage
                    )
            );
        }

        producer.send(messages);
    }

    protected String normalize(final String topic) {
        return Normalizer
                        .normalize(topic, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "")
                        .replaceAll("\\$", "_");
    }

    @Override
    public void onClusterCreated(final Cluster cluster) {
        checkNotNull(cluster);

        final String category = cluster.getName();

        ConsumerConnector consumer = consumerByCategory.get(category);

        if(null == consumer) {
            consumer = consumerFactory.createConnector(category);
            this.consumerByCategory.put(category, consumer);
        }

        final int numStreams = 1;
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("event-consumer-" + category + "-%d").build();
        final List<KafkaStream<byte[], EventMessage>> streams = consumerFactory.createStreams(numStreams, consumer);
        final ExecutorService executorService = Executors.newFixedThreadPool(numStreams, threadFactory);

        for (final KafkaStream<byte[], EventMessage> stream : streams) {
            executorService.submit(new KafkaStreamListener(cluster, stream));
        }
    }

    protected List<ConsumerConnector> getConsumers() {
        return Lists.newArrayList(consumerByCategory.values());
    }

    public void shutdown() {
        for (final ConsumerConnector consumerConnector : consumerByCategory.values()) {
            consumerConnector.shutdown();
        }
    }

    public static class KafkaStreamListener implements Runnable {

        private final Cluster cluster;
        private final KafkaStream<byte[], EventMessage> stream;

        public KafkaStreamListener(final Cluster cluster, final KafkaStream<byte[], EventMessage> stream) {
            this.cluster = checkNotNull(cluster);
            this.stream = checkNotNull(stream);
        }

        @Override
        public void run() {
            final ConsumerIterator<byte[], EventMessage> it = stream.iterator();
            while (it.hasNext()) {
                final EventMessage message = it.next().message();
                cluster.publish(message);
            }
        }
    }
}
