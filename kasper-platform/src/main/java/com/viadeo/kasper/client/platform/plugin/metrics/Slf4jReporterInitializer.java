package com.viadeo.kasper.client.platform.plugin.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

import java.util.concurrent.TimeUnit;

public class Slf4jReporterInitializer implements ReporterInitializer {

    protected static final int DEFAULT_METRICS_TIMEOUT_SECONDS = 20;

    private final Logger logger;

    public Slf4jReporterInitializer(Logger logger) {
        this.logger = Preconditions.checkNotNull(logger);
    }

    @Override
    public void initialize(MetricRegistry metricRegistry) {
        if (logger.isTraceEnabled()) {
            Slf4jReporter reporter = Slf4jReporter
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
