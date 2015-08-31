// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;

public interface ReporterInitializer {

    void initialize(MetricRegistry metricRegistry);

}
