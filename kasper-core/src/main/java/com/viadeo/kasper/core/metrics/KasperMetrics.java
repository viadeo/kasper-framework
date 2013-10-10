// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.DomainResolver;

public final class KasperMetrics {

    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static String namePrefix = "";

    private static DomainResolver domainResolver;

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

    public static String name(final Class<?> clazz, final String...names) {
        final String prefix = namePrefix.isEmpty() ? "" : namePrefix + ".";
        return prefix + MetricRegistry.name(pathForKasperComponent(clazz), names);
    }

    // ------------------------------------------------------------------------

    public static String pathForKasperComponent(final Class<?> clazz) {
        if (null == domainResolver) {
            return clazz.getName();
        }

        final String name = clazz.getSimpleName();
        final Optional<String> domain = domainResolver.getDomainLabelFromClass(clazz);
        if (domain.isPresent()) {
            final Optional<String> type = domainResolver.getComponentTypeName(clazz);
            if (type.isPresent()) {
                return domain.get() + "." + type.get() + "." + name;
            } else {
                return clazz.getName();
            }
        } else {
            return clazz.getName();
        }
    }

    // ------------------------------------------------------------------------

    public static void setDomainResolver(final DomainResolver domainResolver) {
        KasperMetrics.domainResolver = domainResolver;
    }

}
