package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;

/**
 * The PlatformConfiguration interface provides methods to define base components. These components are required in order
 * to initialize a new {@link com.viadeo.kasper.client.platform.Platform}
 */
public interface PlatformConfiguration {

    /**
     * @return the event bus
     */
    KasperEventBus eventBus();

    /**
     * @return the command gateway
     */
    KasperCommandGateway commandGateway();

    /**
     * @return the query gateway
     */
    KasperQueryGateway queryGateway();

    /**
     * @return the metric registry
     */
    MetricRegistry metricRegistry();

    /**
     * @return the configuration
     */
    Config configuration();
}
