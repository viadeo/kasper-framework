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
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperMetrics.class);

    private static KasperMetrics instance;

    public static KasperMetrics instance() {
        if (null == instance) {
            instance = new KasperMetrics();
        }
        return instance;
    }

    // ------------------------------------------------------------------------

    /* Static access to the KasperMetrics instance */
    public static void setNamePrefix(final String prefix) { instance()._setNamePrefix(prefix); }
    public static String name(final String name, final String...names) { return instance()._name(name, names); }
    public static String name(final Class clazz, final String...names) { return instance()._name(clazz, names); }
    public static String name(final MetricNameStyle style, final Class clazz, final String...names) { return instance()._name(style, clazz, names); }
    public static String name(final MetricNameStyle style, final Context context, final Class clazz, final String...names) { return instance()._name(style, context, clazz, names); }
    public static void setResolverFactory(final ResolverFactory resolverFactory) { instance()._setResolverFactory(resolverFactory); }
    public static void unsetResolverFactory() { instance()._unsetResolverFactory(); }
    public static void clearCache() { instance()._clearCache(); }
    public static MetricRegistry getMetricRegistry() { return instance().getRegistry(); }
    public static void setMetricRegistry(MetricRegistry metricRegistry) { instance().setRegistry(metricRegistry); }

    // ------------------------------------------------------------------------

    private String namePrefix = "";
    private ConcurrentMap<MetricNameKey, String> pathCache = Maps.newConcurrentMap();
    private ResolverFactory resolverFactory;
    private MetricRegistry metricRegistry;

    KasperMetrics() { /* Utility class */ }

    // ------------------------------------------------------------------------

    public MetricRegistry getRegistry() {
        if(null == metricRegistry) {
            LOGGER.warn("The metric registry is not initialized.");
            this.metricRegistry = new MetricRegistry();
        }
        return metricRegistry;
    }

    public void setRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
    }

    public void _setNamePrefix(final String prefix) {
        namePrefix = checkNotNull(prefix);
    }

    public String _name(final String name, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(name, checkNotNull(names));
    }

    public String _name(final Class clazz, final String...names) {
        return this._name(MetricNameStyle.DOMAIN_TYPE_COMPONENT, clazz, checkNotNull(names));
    }

    public String _name(final MetricNameStyle style, final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(
                pathForKasperComponent(
                        checkNotNull(style),
                        Contexts.empty(),
                        checkNotNull(clazz)
                ),
                checkNotNull(names)
        );
    }

    public String _name(final MetricNameStyle style, final Context context, final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(
                pathForKasperComponent(
                        checkNotNull(style),
                        checkNotNull(context),
                        checkNotNull(clazz)
                ),
                checkNotNull(names)
        );
    }

    @SuppressWarnings("unchecked")
    protected String pathForKasperComponent(final MetricNameStyle style, final Context context, final Class clazz) {
        checkNotNull(context);
        final MetricNameKey key = new MetricNameKey(checkNotNull(style), checkNotNull(clazz));

        if (pathCache.containsKey(key)) {
            return pathCache.get(key);
        }

        String path = clazz.getName().toLowerCase();

        if (null != resolverFactory && style != MetricNameStyle.NONE) {
            final Optional<Resolver> resolver = resolverFactory.getResolverFromClass(clazz);

            if (resolver.isPresent()) {
                final String domainName = resolver.get().getDomainLabel(clazz);
                final String type = resolver.get().getTypeName();
                final String name = clazz.getSimpleName();

                switch (style) {
                    case DOMAIN_TYPE_COMPONENT:
                        path = String.format("%s.%s.%s", domainName, type, name).toLowerCase();
                        break;
                    case DOMAIN_TYPE:
                        path = String.format("%s.%s", domainName, type).toLowerCase();
                        break;
                    case CLIENT_TYPE:
                        final String clientId = context.getClientId().or("unknown");
                        path = String.format("client.%s.%s", clientId, type).toLowerCase();
                        break;
                }
            }
        }

        return path;
    }

    public void _setResolverFactory(final ResolverFactory resolverFactory) {
        this.resolverFactory = checkNotNull(resolverFactory);
    }

    public void _unsetResolverFactory() {
        resolverFactory = null;
    }

    public void _clearCache() {
        pathCache.clear();
    }

}
