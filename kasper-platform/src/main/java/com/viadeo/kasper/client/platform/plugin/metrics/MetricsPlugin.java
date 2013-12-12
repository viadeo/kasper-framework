package com.viadeo.kasper.client.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.plugin.Plugin;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;

import java.util.Collections;
import java.util.List;

public class MetricsPlugin implements Plugin {

    private final List<ReporterInitializer> reporterInitializers;

    public MetricsPlugin(ReporterInitializer... reporterInitializers) {
        this.reporterInitializers = Lists.newArrayList();

        Collections.addAll(this.reporterInitializers, reporterInitializers);
    }

    @Override
    public void initialize(Platform platform, MetricRegistry metricRegistry, DomainDescriptor... domainDescriptors) {
        for (ReporterInitializer reporterInitializer : reporterInitializers) {
            reporterInitializer.initialize(metricRegistry);
        }
    }
}
