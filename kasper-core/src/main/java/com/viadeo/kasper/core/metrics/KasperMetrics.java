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

    private static final MetricRegistry REGISTRY = new MetricRegistry();

    public static MetricRegistry getRegistry() {
        return REGISTRY;
    }

    // ------------------------------------------------------------------------

    private String namePrefix = "";
    private ConcurrentMap<Class, String> pathCache = Maps.newConcurrentMap();
    private ResolverFactory resolverFactory;

    // ------------------------------------------------------------------------

    private static KasperMetrics instance;

    KasperMetrics() { /* Utility class */ }

    private static KasperMetrics instance() {
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
    public static String pathForKasperComponent(final Class clazz) { return instance()._pathForKasperComponent(clazz); }
    public static void setResolverFactory(final ResolverFactory resolverFactory) { instance()._setResolverFactory(resolverFactory); }
    public static void unsetResolverFactory() { instance()._unsetResolverFactory(); }
    public static void clearCache() { instance()._clearCache(); }

    // ------------------------------------------------------------------------

    public void _setNamePrefix(final String prefix) {
        namePrefix = prefix;
    }

    public String _name(final String name, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(name, names);
    }

    public String _name(final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(_pathForKasperComponent(clazz), names);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public String _pathForKasperComponent(final Class clazz) {

        if (pathCache.containsKey(clazz)) {
            return pathCache.get(clazz);
        }

        String componentPath = clazz.getName();

        if (null != resolverFactory) {
            final String name = clazz.getSimpleName();

            final Optional<Resolver> resolver = resolverFactory.getResolverFromClass(clazz);
            if (resolver.isPresent()) {
                final String domainName = resolver.get().getDomainLabel(clazz);
                final String type = resolver.get().getTypeName();
                componentPath = domainName + "." + type + "." + name;
            }
        }

        pathCache.put(clazz, componentPath);
        return componentPath;
    }

    // ------------------------------------------------------------------------

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
