// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.util.ErrorHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public final class InstrumentedErrorHandler implements ErrorHandler {

    protected static final Log logger = LogFactory.getLog(InstrumentedErrorHandler.class);
    private final static String ERROR_METER = name(InstrumentedErrorHandler.class, "error");
    private final MetricRegistry metricRegistry;
    private final ConditionalRejectingErrorHandler errorHandler;


    public InstrumentedErrorHandler(ConditionalRejectingErrorHandler errorHandler, MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.errorHandler = errorHandler;
    }

    @Override
    public void handleError(final Throwable t) {
        metricRegistry.meter(ERROR_METER);
        errorHandler.handleError(t);
    }
}
