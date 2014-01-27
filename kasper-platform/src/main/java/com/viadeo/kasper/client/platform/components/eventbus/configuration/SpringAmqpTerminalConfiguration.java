// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.configuration;

import com.typesafe.config.Config;

public class SpringAmqpTerminalConfiguration implements TerminalConfiguration {

    private final String hostname;
    private final Integer port;
    private final String username;
    private final String password;
    private final String vhost;
    private final ExchangeConfiguration exchangeConfiguration;
    private final QueueConfiguration queueConfiguration;

    public SpringAmqpTerminalConfiguration(final Config config) {
        this(
                config.getString("hostname"),
                config.getInt("port"),
                config.getString("username"),
                config.getString("password"),
                config.getString("vhost"),
                new ExchangeConfiguration(config.getConfig("exchange")),
                new QueueConfiguration(config.getConfig("queue"))
        );
    }

    public SpringAmqpTerminalConfiguration(final String hostname,
                                           final Integer port,
                                           final String username,
                                           final String password,
                                           final String vhost,
                                           final ExchangeConfiguration exchangeConfiguration,
                                           final QueueConfiguration queueConfiguration

    ) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.vhost = vhost;
        this.exchangeConfiguration = exchangeConfiguration;
        this.queueConfiguration = queueConfiguration;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVhost() {
        return vhost;
    }

    public ExchangeConfiguration getExchangeConfiguration() {
        return exchangeConfiguration;
    }

    public QueueConfiguration getQueueConfiguration() {
        return queueConfiguration;
    }
}
