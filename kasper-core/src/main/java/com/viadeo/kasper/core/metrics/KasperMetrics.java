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
    private static String namePrefix = "";

    private static ConcurrentMap<Class, String> pathCache = Maps.newConcurrentMap();

    private static ResolverFactory resolverFactory;

    // ------------------------------------------------------------------------

    private KasperMetrics() { /* Utility class */ }

    // ------------------------------------------------------------------------

    public static MetricRegistry getRegistry() {
        return REGISTRY;
    }

    // ------------------------------------------------------------------------

    public static void setNamePrefix(final String prefix) {
        namePrefix = prefix;
    }

    public static String name(final String name, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(name, names);
    }

    public static String name(final Class clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(pathForKasperComponent(clazz), names);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static String pathForKasperComponent(final Class clazz) {

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

    public static void setResolverFactory(final ResolverFactory resolverFactory) {
        KasperMetrics.resolverFactory = checkNotNull(resolverFactory);
    }

    public static void unsetResolverFactory(){
        KasperMetrics.resolverFactory = null;
    }

    public static void clearCache() {
        pathCache.clear();
    }

}
