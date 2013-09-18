// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;

public final class KasperMetrics {

    private static final MetricRegistry REGISTRY = new MetricRegistry();
    private static String namePrefix = "";

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
        return prefix + MetricRegistry.name(clazz, names);
    }

}
