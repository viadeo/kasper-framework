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
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperMetrics {

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

    public MetricRegistry getRegistry() {
        if(null == metricRegistry) {
            throw new IllegalStateException("The metric registry is not initialized.");
        }
        return metricRegistry;
    }

    public void setRegistry(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void _setNamePrefix(final String prefix) {
        namePrefix = prefix;
    }

    public String _name(final String name, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(name, names);
    }

    public String _name(final Class clazz, final String...names) {
        return this._name(MetricNameStyle.DOMAIN_TYPE_COMPONENT, clazz, names);
    }

    public String _name(final MetricNameStyle style, final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(pathForKasperComponent(style, clazz), names);
    }


    @SuppressWarnings("unchecked")
    protected String pathForKasperComponent(final MetricNameStyle style, final Class clazz) {
        final MetricNameKey key = new MetricNameKey(style, clazz);

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
