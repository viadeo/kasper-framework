// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================securityConfigurationBuilder
package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;

import java.util.List;
import java.util.Map;

import static com.viadeo.kasper.client.platform.Platform.ExtraComponentKey;

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
    CommandGateway commandGateway();

    /**
     * @return the query gateway
     */
    QueryGateway queryGateway();

    /**
     * @return the metric registry
     */
    MetricRegistry metricRegistry();

    /**
     * @return the configuration
     */
    Config configuration();

    /**
     * @return the extra components
     */
    Map<ExtraComponentKey, Object> extraComponents();

    /**
     * @return the list of interceptor factories dedicated to the command side
     */
    List<CommandInterceptorFactory> commandInterceptorFactories();

    /**
     * @return the list of interceptor factories dedicated to the query side
     */
    List<QueryInterceptorFactory> queryInterceptorFactories();

}
