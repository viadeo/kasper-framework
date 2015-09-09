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

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class BuilderContext {

    private final Config configuration;
    private final KasperEventBus eventBus;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Map<ExtraComponent.Key, ExtraComponent> extraComponents;

    // --------------------------------------------------------------------

    public BuilderContext(final Config configuration,
                          final KasperEventBus eventBus,
                          final CommandGateway commandGateway,
                          final QueryGateway queryGateway,
                          final MetricRegistry metricRegistry,
                          final List<ExtraComponent> extraComponents
    ) {
        this.configuration = checkNotNull(configuration);
        this.eventBus = checkNotNull(eventBus);
        this.commandGateway = checkNotNull(commandGateway);
        this.queryGateway = checkNotNull(queryGateway);
        this.metricRegistry = checkNotNull(metricRegistry);

        checkNotNull(extraComponents);

        this.extraComponents = Maps.newHashMap();
        for (final ExtraComponent extraComponent : extraComponents) {
            this.extraComponents.put(extraComponent.getKey(), extraComponent);
        }
    }

    // --------------------------------------------------------------------

    public Config getConfiguration() {
        return configuration;
    }

    public KasperEventBus getEventBus() {
        return eventBus;
    }

    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<E> getExtraComponent(final String name, final Class<E> clazz) {
        return Optional.fromNullable((E) extraComponents.get(new ExtraComponent.Key(name, clazz)));
    }

    public List<ExtraComponent> getExtraComponents() {
        return Lists.newArrayList(extraComponents.values());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(configuration, eventBus, commandGateway, queryGateway, metricRegistry, extraComponents);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final BuilderContext other = (BuilderContext) obj;
        return Objects.equal(this.configuration, other.configuration) && Objects.equal(this.eventBus, other.eventBus) && Objects.equal(this.commandGateway, other.commandGateway) && Objects.equal(this.queryGateway, other.queryGateway) && Objects.equal(this.metricRegistry, other.metricRegistry) && Objects.equal(this.extraComponents, other.extraComponents);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("configuration", configuration)
                .add("eventBus", eventBus)
                .add("commandGateway", commandGateway)
                .add("queryGateway", queryGateway)
                .add("metricRegistry", metricRegistry)
                .add("extraComponents", extraComponents)
                .toString();
    }
}
