package com.viadeo.kasper.core.component.eventbus;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

public interface AMQPTopologyListener {
    void onQueueCreated(Queue queue);
    void onQueueDeleted(String name);

    void onExchangeCreated(Exchange exchange);
    void onExchangeDeleted(String name);

    void onBindingCreated(Binding binding);
    void onBindingDeleted(Binding binding);
}
