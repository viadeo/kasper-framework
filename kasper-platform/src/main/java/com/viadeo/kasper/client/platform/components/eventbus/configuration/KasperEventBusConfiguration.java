// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class KasperEventBusConfiguration {

    public static final String CLUSTER_SELECTOR_CONFIG_PATH = "clusterSelector";
    public static final String AMQP_TERMINAL_CONFIG_PATH = "terminal.amqp";
    public static final String KAFKA_TERMINAL_CONFIG_PATH = "terminal.kafka";

    private ClusterSelectorConfiguration clusterSelectorConfiguration;
    private TerminalConfiguration terminalConfiguration;

    public KasperEventBusConfiguration(){
        this(ConfigFactory.empty());
    }

    public KasperEventBusConfiguration(final Config config) {
        if (config.hasPath(CLUSTER_SELECTOR_CONFIG_PATH)) {
            setClusterSelectorConfiguration(new ClusterSelectorConfiguration(config.getConfig(CLUSTER_SELECTOR_CONFIG_PATH)));
        }

        if (config.hasPath(AMQP_TERMINAL_CONFIG_PATH)) {
            setTerminalConfiguration(new SpringAmqpTerminalConfiguration(config.getConfig(AMQP_TERMINAL_CONFIG_PATH)));
        } else if (config.hasPath(KAFKA_TERMINAL_CONFIG_PATH)) {
            setTerminalConfiguration(new KafkaTerminalConfiguration(config.getConfig(KAFKA_TERMINAL_CONFIG_PATH)));
        }
    }

    public void setClusterSelectorConfiguration(final ClusterSelectorConfiguration clusterSelectorConfiguration) {
        this.clusterSelectorConfiguration = clusterSelectorConfiguration;
    }

    public void setTerminalConfiguration(final TerminalConfiguration terminalConfiguration) {
        this.terminalConfiguration = terminalConfiguration;
    }

    public ClusterSelectorConfiguration getClusterSelectorConfiguration() {
        return clusterSelectorConfiguration;
    }

    public TerminalConfiguration getTerminalConfiguration() {
        return terminalConfiguration;
    }

}
