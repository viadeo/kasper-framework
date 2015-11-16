// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.plugin.metrics;

import com.google.common.collect.Lists;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.plugin.PluginAdapter;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MetricsPlugin extends PluginAdapter {

    private final List<ReporterInitializer> reporterInitializers;

    // ------------------------------------------------------------------------

    public MetricsPlugin(final ReporterInitializer... reporterInitializers) {
        this.reporterInitializers = Lists.newArrayList();
        Collections.addAll(this.reporterInitializers, checkNotNull(reporterInitializers));
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final PlatformContext platformContext) {
        for (final ReporterInitializer reporterInitializer : reporterInitializers) {
            reporterInitializer.initialize(platformContext.getMetricRegistry());
        }
    }
}
