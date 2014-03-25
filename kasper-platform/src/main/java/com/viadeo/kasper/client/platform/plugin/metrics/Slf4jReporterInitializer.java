// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class Slf4jReporterInitializer implements ReporterInitializer {

    protected static final int DEFAULT_METRICS_TIMEOUT_SECONDS = 20;

    private final Logger logger;

    //-------------------------------------------------------------------------

    public Slf4jReporterInitializer(final Logger logger) {
        this.logger = checkNotNull(logger);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final MetricRegistry metricRegistry) {
        if (logger.isTraceEnabled()) {
            final Slf4jReporter reporter = Slf4jReporter
                .forRegistry(KasperMetrics.getMetricRegistry())
                .outputTo(logger)
                .markWith(MarkerFactory.getMarker("TRACE"))
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.SECONDS)
                .build();

            reporter.start(DEFAULT_METRICS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

}
