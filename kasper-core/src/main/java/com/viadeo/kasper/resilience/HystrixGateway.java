package com.viadeo.kasper.resilience;


import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class.
 * Centralize methods for different child gateway.
 * Close for modification but open for extension
 */
public abstract class HystrixGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixGateway.class);

    public static final String SYSTEM_PROPERTY_HYSTRIX_ENABLE = "hystrixEnable";

    @VisibleForTesting
    static boolean metricNotInitialized = true;

    private AtomicLong fallbackCount = new AtomicLong(0); // when no metric is available

    /**
     * Register metrics on object creation
     */
    protected HystrixGateway(MetricRegistry metricRegistry) {
        // register metrics plugin
        registerMetricPlugin(metricRegistry);
    }

    /**
     * Increment fallback count and do something later (maybe)
     * @param commandName hystrix command name
     */
    protected final void reportFallback(String commandName) {
        LOGGER.error("call to [{}] failed due to timeout", commandName);
        fallbackCount.incrementAndGet();
    }

    /**
     * Temporary method. Sorry for that... but there is too much log, i don't see anything ...
     * This will be removed when really tested with stress tests
     * Log hystrix activation
     */
    @Deprecated
    public static void logActivation() {
        LOGGER.info("//------------------------------------------------------------");
        LOGGER.info("//------------------ Hystrix activated !!! -------------------");
        LOGGER.info("//------------------------------------------------------------");
    }

    /**
     * Look in system property for 'hystrixEnable' variable.<br>
     * Possible values: "true", "false".<br>
     * If true, log message to show activation (removed later)<br>
     * @return true if activated, false otherwise
     */
    public static boolean isActivated() {
        if ( "true".equals(System.getProperty(SYSTEM_PROPERTY_HYSTRIX_ENABLE)) ) {
            logActivation();
            return true;
        }
        return false;
    }

    @VisibleForTesting
    static synchronized void registerMetricPlugin(MetricRegistry metricRegistry) {
        if (metricNotInitialized && metricRegistry != null) {
            try {
                HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metricRegistry));
                metricNotInitialized = false;
            } catch (IllegalStateException e) {
                LOGGER.error("metrics will not be available for hystrix gateway", e);
            }
        }

    }

    // ---------------------------- ACCESSORS / MUTATORS ------------------------------------------

    /**
     * return fallback count
     * @return
     */
    @SuppressWarnings("unused")
    public final long getFallbackCount() {
        return fallbackCount.get();
    }
}
