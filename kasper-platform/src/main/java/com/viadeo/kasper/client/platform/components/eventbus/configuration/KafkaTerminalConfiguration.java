// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class KafkaTerminalConfiguration implements TerminalConfiguration {

    private final ConsumerConfiguration consumerConfiguration;
    private final ProducerConfiguration producerConfiguration;

    public KafkaTerminalConfiguration(final Config config) {
        this(
                new ConsumerConfiguration(config.getConfig("consumer")),
                new ProducerConfiguration(config.getConfig("producer"))
        );
    }

    public KafkaTerminalConfiguration(
            final ConsumerConfiguration consumerConfiguration,
            final ProducerConfiguration producerConfiguration
    ) {
        this.consumerConfiguration = checkNotNull(consumerConfiguration);
        this.producerConfiguration = checkNotNull(producerConfiguration);
    }

    public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration;
    }

    public ConsumerConfiguration getConsumerConfiguration() {
        return consumerConfiguration;
    }

    public static class ConsumerConfiguration {

        private final String zookeeperConnect;
        private final String zookeeperSessionTimeoutInMillis;
        private final String zookeeperSyncTimeInMillis;
        private final String autoCommitIntervalInMillis;

        public ConsumerConfiguration(final Config config) {
            this(
                    config.getString("zookeeper.connect"),
                    config.getString("zookeeper.session.timeout.ms"),
                    config.getString("zookeeper.sync.time.ms"),
                    config.getString("auto.commit.interval.ms")
            );
        }

        public ConsumerConfiguration(
                final String zookeeperConnect,
                final String zookeeperSessionTimeoutInMillis,
                final String zookeeperSyncTimeInMillis,
                final String autoCommitIntervalInMillis
        ) {
            this.zookeeperConnect = zookeeperConnect;
            this.zookeeperSessionTimeoutInMillis = zookeeperSessionTimeoutInMillis;
            this.zookeeperSyncTimeInMillis = zookeeperSyncTimeInMillis;
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

        public String getAutoCommitIntervalInMillis() {
            return autoCommitIntervalInMillis;
        }

        public Properties toProperties(){
            final Properties props = new Properties();
            props.put("zookeeper.connect", getZookeeperConnect());
            props.put("zookeeper.session.timeout.ms", getZookeeperSessionTimeoutInMillis());
            props.put("zookeeper.sync.time.ms", getZookeeperSyncTimeInMillis());
            props.put("auto.commit.interval.ms", getAutoCommitIntervalInMillis());
            return props;
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

        public Properties toProperties() {
            final Properties props = new Properties();
            props.put("metadata.broker.list", getBrokerList());
            return props;
        }
    }
}
