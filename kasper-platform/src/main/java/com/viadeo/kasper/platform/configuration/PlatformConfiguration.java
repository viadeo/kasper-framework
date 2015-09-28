// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;

import java.util.List;

/**
 * The PlatformConfiguration interface provides methods to define base components. These components are required in order
 * to initialize a new {@link com.viadeo.kasper.platform.Platform}
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
     * @return the saga manager
     */
    SagaManager sagaManager();

    /**
     * @return the configuration
     */
    Config configuration();

    /**
     * @return the extra components
     */
    List<ExtraComponent> extraComponents();

    /**
     * @return the list of interceptor factories dedicated to the command side
     */
    List<CommandInterceptorFactory> commandInterceptorFactories();

    /**
     * @return the list of interceptor factories dedicated to the query side
     */
    List<QueryInterceptorFactory> queryInterceptorFactories();

    /**
     * @return the list of interceptor factories dedicated to the event side
     */
    List<EventInterceptorFactory> eventInterceptorFactories();

    /**
     * @return the domain descriptor factory
     */
    DomainDescriptorFactory domainDescriptorFactory();

}
