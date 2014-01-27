// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.DefaultDecoder;
import org.axonframework.domain.EventMessage;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Consumer {

    private final ConsumerConnector consumer;
    private final String topic;
    private final List<KafkaMessageListener> messageListeners;

    public Consumer(final Properties properties, final String topic) {
        this.topic = topic;
        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
        this.messageListeners = Lists.newArrayList();
    }

    public void consume() {
        final int nbOfThreads = 2;

        final Map<String, Integer> topicCountMap = Maps.newHashMap();
        topicCountMap.put(topic, nbOfThreads);

        final Map<String, List<KafkaStream<byte[], EventMessage>>> consumerMap = consumer.createMessageStreams(
                topicCountMap,
                new DefaultDecoder(null),
                new EventMessageSerializer()
        );

        final List<KafkaStream<byte[], EventMessage>> streams = consumerMap.get(topic);

        final ExecutorService executor = Executors.newFixedThreadPool(nbOfThreads);

        for (int threadNumber = 0; threadNumber < nbOfThreads; threadNumber++) {
            final int threadNb = threadNumber;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    final ConsumerIterator<byte[], EventMessage> it = streams.get(threadNb).iterator();

                    while (it.hasNext()) {
                        final EventMessage eventMessage = it.next().message();

                        fireMessageReceived(eventMessage);
                    }
                }
            });
        }
    }

    public void addMessageListener(final KafkaMessageListener messageListener) {
        this.messageListeners.add(checkNotNull(messageListener));
    }

    protected void fireMessageReceived(final EventMessage eventMessage) {
        checkNotNull(eventMessage);
        for (final KafkaMessageListener messageListener : messageListeners) {
            messageListener.messageReceived(eventMessage);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("group.id", "0");
        props.put("auto.commit.interval.ms", "1000");

        final Consumer consumer = new Consumer(props, "sampleTopic");
        consumer.consume();
    }

}
