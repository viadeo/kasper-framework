// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;

public final class KasperMetrics {

    private static MetricRegistry metricRegistry = new MetricRegistry();

    public static MetricRegistry getRegistry() {
        return metricRegistry;
    }

}
