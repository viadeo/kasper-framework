package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

public class MetricConnectionListener implements ConnectionListener {

    private final MetricRegistry metricRegistry;

    public MetricConnectionListener(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void onCreate(Connection connection) {
        metricRegistry.counter(MetricRegistry.name(MetricConnectionListener.class, "create"));
    }

    @Override
    public void onClose(Connection connection) {
        metricRegistry.counter(MetricRegistry.name(MetricConnectionListener.class, "close"));
    }
}
