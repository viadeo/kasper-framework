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
    public static final String TERMINAL_CONFIG_PATH = "terminal";

    private ClusterSelectorConfiguration clusterSelectorConfiguration;
    private TerminalConfiguration terminalConfiguration;

    public KasperEventBusConfiguration(){
        this(ConfigFactory.empty());
    }

    public KasperEventBusConfiguration(final Config config) {

        if (config.hasPath(CLUSTER_SELECTOR_CONFIG_PATH)) {
            setClusterSelectorConfiguration(new ClusterSelectorConfiguration(config.getConfig(CLUSTER_SELECTOR_CONFIG_PATH)));
        }

        if (config.hasPath(TERMINAL_CONFIG_PATH)) {
            setTerminalConfiguration(new TerminalConfiguration(config.getConfig(TERMINAL_CONFIG_PATH)));
        }
    }

    public void setClusterSelectorConfiguration(ClusterSelectorConfiguration clusterSelectorConfiguration) {
        this.clusterSelectorConfiguration = clusterSelectorConfiguration;
    }

    public void setTerminalConfiguration(TerminalConfiguration terminalConfiguration) {
        this.terminalConfiguration = terminalConfiguration;
    }

    public ClusterSelectorConfiguration getClusterSelectorConfiguration() {
        return clusterSelectorConfiguration;
    }

    public TerminalConfiguration getTerminalConfiguration() {
        return terminalConfiguration;
    }

}
