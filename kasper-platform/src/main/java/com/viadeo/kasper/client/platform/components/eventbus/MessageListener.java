package com.viadeo.kasper.client.platform.components.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;


public class MessageListener {

    public static final String HANDLE_MESSAGE_COUNT_METRIC = KasperMetrics.name(MessageListener.class, "handle-message", "count");
    public static final String HANDLE_MESSAGE_TIME_METRIC = KasperMetrics.name(MessageListener.class, "handle-message", "time");

    private final EventListener listener;
    private final MetricRegistry metricRegistry;

    public MessageListener(EventListener listener, MetricRegistry metricRegistry) {
        this.listener = listener;
        this.metricRegistry = metricRegistry;
    }

    @SuppressWarnings("unused")
    public void handleMessage(EventMessage eventMessage) {
        metricRegistry.counter(HANDLE_MESSAGE_COUNT_METRIC).inc();
        metricRegistry.histogram(HANDLE_MESSAGE_TIME_METRIC).update(timeTaken(eventMessage));
        listener.handle(eventMessage);
    }

    private long timeTaken(EventMessage eventMessage) {
        return System.currentTimeMillis() - eventMessage.getTimestamp().getMillis();
    }
}
