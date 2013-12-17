// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.plugin.Plugin;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MetricsPlugin implements Plugin {

    private final List<ReporterInitializer> reporterInitializers;

    // ------------------------------------------------------------------------

    public MetricsPlugin(final ReporterInitializer... reporterInitializers) {
        this.reporterInitializers = Lists.newArrayList();
        Collections.addAll(this.reporterInitializers, checkNotNull(reporterInitializers));
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {
        for (final ReporterInitializer reporterInitializer : reporterInitializers) {
            reporterInitializer.initialize(metricRegistry);
        }
    }

}
