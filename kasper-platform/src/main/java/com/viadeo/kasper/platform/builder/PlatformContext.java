// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.viadeo.kasper.platform.Meta;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlatformContext {

    private final Config configuration;
    private final KasperEventBus eventBus;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Meta meta;
    private final Map<ExtraComponent.Key, ExtraComponent> extraComponents;

    // --------------------------------------------------------------------

    public PlatformContext(final Config configuration,
                           final KasperEventBus eventBus,
                           final CommandGateway commandGateway,
                           final QueryGateway queryGateway,
                           final MetricRegistry metricRegistry,
                           final List<ExtraComponent> extraComponents,
                           final Meta meta
    ) {
        this.meta = checkNotNull(meta);
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

    public Meta getMeta() {
        return meta;
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
        return Objects.hashCode(configuration, eventBus, commandGateway, queryGateway, metricRegistry, meta, extraComponents);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PlatformContext other = (PlatformContext) obj;
        return Objects.equal(this.configuration, other.configuration) && Objects.equal(this.eventBus, other.eventBus) && Objects.equal(this.commandGateway, other.commandGateway) && Objects.equal(this.queryGateway, other.queryGateway) && Objects.equal(this.metricRegistry, other.metricRegistry) && Objects.equal(this.meta, other.meta) && Objects.equal(this.extraComponents, other.extraComponents);
    }
}
