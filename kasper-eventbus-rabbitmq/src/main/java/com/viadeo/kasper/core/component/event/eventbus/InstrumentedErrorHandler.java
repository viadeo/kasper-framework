// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public final class InstrumentedErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentedErrorHandler.class);
    private static final String ERROR_METER = name(InstrumentedErrorHandler.class, "error");

    private final MetricRegistry metricRegistry;
    private final ErrorHandler errorHandler;

    public InstrumentedErrorHandler(ErrorHandler errorHandler, MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.errorHandler = errorHandler;
    }

    @Override
    public void handleError(final Throwable t) {
        metricRegistry.meter(ERROR_METER);
        if (t.getCause() != null) {
            LOGGER.error("Unable to consume event from amqp : {}", t.getCause().getMessage(), t);
        } else {
            LOGGER.error("Unable to consume event from amqp", t);
        }
        errorHandler.handleError(t);
    }
}
