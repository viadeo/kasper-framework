package com.viadeo.kasper.client.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;

public interface ReporterInitializer {
    void initialize(MetricRegistry metricRegistry);
}
