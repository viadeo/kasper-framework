// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.builder;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpringBuilderContext extends BuilderContext {

    private final Config configuration;
    private final KasperEventBus eventBus;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Map<ExtraComponent.Key, ExtraComponent> extraComponents;

    // --------------------------------------------------------------------

    public SpringBuilderContext(final KasperPlatformConfiguration platformConfiguration,
                                final List<ExtraComponent> extraComponents
    ) {
        this(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents
        );
    }

}
