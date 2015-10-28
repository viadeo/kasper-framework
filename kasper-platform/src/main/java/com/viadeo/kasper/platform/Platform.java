// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;

/**
 * The Kasper platform
 * <p>
 * This interface represent the main entry point to your platform front components,
 * the Command and Query gateways from which your can then send commands and queries,
 * or even send Events.
 * </p>
 */
public interface Platform {

    /**
     * @return the Command gateway to use in order to send commands to the platform
     */
    CommandGateway getCommandGateway();

    /**
     * @return the query gateway to use in order to send queries to the platform
     */
    QueryGateway getQueryGateway();

    /**
     * @return the event bus used by the platform
     */
    KasperEventBus getEventBus();

    /**
     * @return the metric registry used by the platform
     */
    MetricRegistry getMetricRegistry();

    /**
     * @return the meta information of the platform
     */
    Meta getMeta();

    /**
     * Starts the platform
     * @return the started platform
     */
    Platform start();

    /**
     * Stops the platform
     * @return the stopped platform
     */
    Platform stop();

    // ========================================================================

    /**
     * The platform builder
     */
    interface Builder {

        /**
         * Builds the platform.
         *
         * @return an instantiated platform
         */
        Platform build();

    }
}
