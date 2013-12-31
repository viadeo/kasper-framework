// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandBus;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.command.interceptor.CommandValidationInterceptorFactory;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.cqrs.query.interceptor.CacheInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryFilterInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryValidationInterceptorFactory;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;

import java.util.List;

import static com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus.Policy;

/**
 * The KasperPlatformConfiguration class provides default implementation of the components required by the  {@link com.viadeo.kasper.client.platform.Platform}.
 *
 * @see com.viadeo.kasper.client.platform.Platform.Builder
 */
public class KasperPlatformConfiguration implements PlatformConfiguration {

    private final KasperEventBus eventBus;
    private final KasperQueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Config configuration;
    private final KasperCommandGateway commandGateway;
    private final List<CommandInterceptorFactory> commandInterceptorFactories;
    private final List<QueryInterceptorFactory> queryInterceptorFactories;

    // ------------------------------------------------------------------------

    public KasperPlatformConfiguration() {
        this(null);
    }

    public KasperPlatformConfiguration(SecurityConfiguration securityConfiguration) {
        this.eventBus = new KasperEventBus(Policy.ASYNCHRONOUS);
        this.queryGateway = new KasperQueryGateway();
        this.metricRegistry = new MetricRegistry();
        this.configuration = ConfigFactory.empty();

        UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();

        KasperCommandBus commandBus = new KasperCommandBus();
        commandBus.setUnitOfWorkFactory(uowFactory);

        this.commandGateway = new KasperCommandGateway(commandBus);

        this.commandInterceptorFactories = Lists.<CommandInterceptorFactory>newArrayList(
                new CommandValidationInterceptorFactory()
        );

        this.queryInterceptorFactories =  Lists.newArrayList(
                new CacheInterceptorFactory(),
                new QueryValidationInterceptorFactory(),
                new QueryFilterInterceptorFactory()
        );
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperEventBus eventBus() {
        return eventBus;
    }

    @Override
    public KasperCommandGateway commandGateway() {
        return commandGateway;
    }

    @Override
    public KasperQueryGateway queryGateway() {
        return queryGateway;
    }

    @Override
    public MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    @Override
    public Config configuration() {
        return configuration;
    }

    @Override
    public List<CommandInterceptorFactory> commandInterceptorFactories() {
        return commandInterceptorFactories;
    }

    @Override
    public List<QueryInterceptorFactory> queryInterceptorFactories() {
        return queryInterceptorFactories;
    }

}
