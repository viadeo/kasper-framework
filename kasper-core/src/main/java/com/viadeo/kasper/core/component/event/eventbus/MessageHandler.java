package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    public static final String HANDLE_MESSAGE_COUNT_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "count");
    public static final String HANDLE_MESSAGE_ERROR_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "error");
    public static final String HANDLE_MESSAGE_TIME_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "time");

    private final EventListener eventListener;
    private final MetricRegistry metricRegistry;
    private final boolean enabledMessageHandling;

    public MessageHandler(EventListener eventListener, MetricRegistry metricRegistry, boolean enabledMessageHandling) {
        this.eventListener = eventListener;
        this.metricRegistry = metricRegistry;
        this.enabledMessageHandling = enabledMessageHandling;
    }

    @SuppressWarnings("unused")
    public void handleMessage(EventMessage eventMessage) {
        metricRegistry.counter(HANDLE_MESSAGE_COUNT_METRIC).inc();
        metricRegistry.histogram(HANDLE_MESSAGE_TIME_METRIC).update(timeTaken(eventMessage));

        MDC.setContextMap(
                Maps.transformEntries(
                        eventMessage.getMetaData(),
                        new Maps.EntryTransformer<String, Object, String>() {
                            @Override
                            public String transformEntry( String key, Object value) {
                                return String.valueOf(value);
                            }
                        }
                )
        );

        try {
            if (enabledMessageHandling) {
                eventListener.handle(eventMessage);
            }
        } catch (Exception t) {
            metricRegistry.counter(HANDLE_MESSAGE_ERROR_METRIC).inc();
            LOGGER.warn("failed to handle event message by '{}'", eventListener.getClass().getName(), t);
            throw new MessageHandlerException(eventListener.getClass(), t);
        }
    }

    private long timeTaken(EventMessage eventMessage) {
        return System.currentTimeMillis() - eventMessage.getTimestamp().getMillis();
    }
}
