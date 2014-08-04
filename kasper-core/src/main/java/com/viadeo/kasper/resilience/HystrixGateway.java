// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
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

    // ------------------------------------------------------------------------

    /**
     * Register metrics on object creation
     */
    protected HystrixGateway(final MetricRegistry metricRegistry) {
        // register metrics plugin
        registerMetricPlugin(metricRegistry, HystrixPlugins.getInstance());
    }

    // ------------------------------------------------------------------------

    /**
     * Increment fallback count and do something later (maybe)
     * @param commandName hystrix command name
     */
    protected final void reportFallback(final String commandName) {
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
    static synchronized void registerMetricPlugin(final MetricRegistry metricRegistry, final HystrixPlugins hystrixPlugins) {
        if (metricNotInitialized && (null != metricRegistry)) {
            try {
                hystrixPlugins.registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metricRegistry));
                metricNotInitialized = false;
            } catch (final IllegalStateException e) {
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
