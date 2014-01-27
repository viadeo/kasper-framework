// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;

public class KafkaTerminalConfiguration implements TerminalConfiguration {

    private final ConsumerConfiguration consumerConfiguration;
    private final ProducerConfiguration producerConfiguration;
    private final String topic;

    public KafkaTerminalConfiguration(final Config config) {
        this(
                config.getString("topic"),
                new ConsumerConfiguration(config.getConfig("consumer")),
                new ProducerConfiguration(config.getConfig("producer"))
        );
    }

    public KafkaTerminalConfiguration(
            final String topic,
            final ConsumerConfiguration consumerConfiguration,
            final ProducerConfiguration producerConfiguration
    ) {
        this.topic = topic;
        this.consumerConfiguration = consumerConfiguration;
        this.producerConfiguration = producerConfiguration;
    }

    public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration;
    }

    public ConsumerConfiguration getConsumerConfiguration() {
        return consumerConfiguration;
    }

    public String getTopic() {
        return topic;
    }


    public static class ConsumerConfiguration {

        private final String zookeeperConnect;
        private final String zookeeperSessionTimeoutInMillis;
        private final String zookeeperSyncTimeInMillis;
        private final String groupId;
        private final String autoCommitIntervalInMillis;

        public ConsumerConfiguration(final Config config) {
            this(
                    config.getString("zookeeper.connect"),
                    config.getString("zookeeper.session.timeout.ms"),
                    config.getString("zookeeper.sync.time.ms"),
                    config.getString("group.id"),
                    config.getString("auto.commit.interval.ms")
            );
        }

        public ConsumerConfiguration(
                final String zookeeperConnect,
                final String zookeeperSessionTimeoutInMillis,
                final String zookeeperSyncTimeInMillis,
                final String groupId,
                final String autoCommitIntervalInMillis
        ) {
            this.zookeeperConnect = zookeeperConnect;
            this.zookeeperSessionTimeoutInMillis = zookeeperSessionTimeoutInMillis;
            this.zookeeperSyncTimeInMillis = zookeeperSyncTimeInMillis;
            this.groupId = groupId;
            this.autoCommitIntervalInMillis = autoCommitIntervalInMillis;
        }

        public String getZookeeperConnect() {
            return zookeeperConnect;
        }

        public String getZookeeperSessionTimeoutInMillis() {
            return zookeeperSessionTimeoutInMillis;
        }

        public String getZookeeperSyncTimeInMillis() {
            return zookeeperSyncTimeInMillis;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getAutoCommitIntervalInMillis() {
            return autoCommitIntervalInMillis;
        }
    }

    public static class ProducerConfiguration {

        private final String brokerList;

        public ProducerConfiguration(final Config config) {
            this(config.getString("metadata.broker.list"));
        }

        public ProducerConfiguration(final String brokerList) {
            this.brokerList = brokerList;
        }

        public String getBrokerList() {
            return brokerList;
        }
    }
}
