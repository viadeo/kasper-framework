// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.Channel;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.amqp.AMQPMessage;
import org.axonframework.eventhandling.amqp.SpringAMQPTerminal;
import org.springframework.amqp.core.*;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

public class InstrumentedSpringAmqpTerminal extends SpringAMQPTerminal {

    public static final String DO_SEND_MESSAGE_COUNT = name(InstrumentedSpringAmqpTerminal.class, "send", "count");
    public static final String DO_SEND_MESSAGE_BODY_SIZE = name(InstrumentedSpringAmqpTerminal.class, "send", "bodySize");

    private final AmqpAdmin rabbitAdmin;
    private final String deadLetterExchangeNameFormat;
    private final String deadLetterQueueNameFormat;
    private final String exchangeName;
    private final boolean queueDurable;
    private final MetricRegistry metricsRegistry;

    public InstrumentedSpringAmqpTerminal(final AmqpAdmin rabbitAdmin,
                                          final String deadLetterExchangeNameFormat,
                                          final String deadLetterQueueNameFormat,
                                          final String exchangeName,
                                          final boolean queueDurable,
                                          final MetricRegistry metricsRegistry) {
        this.metricsRegistry = checkNotNull(metricsRegistry);
        this.rabbitAdmin = checkNotNull(rabbitAdmin);
        this.deadLetterExchangeNameFormat = checkNotNull(deadLetterExchangeNameFormat);
        this.deadLetterQueueNameFormat = checkNotNull(deadLetterQueueNameFormat);
        this.exchangeName = checkNotNull(exchangeName);
        this.queueDurable = checkNotNull(queueDurable);
    }

    @Override
    protected void doSendMessage(Channel channel, AMQPMessage amqpMessage) throws IOException {

        metricsRegistry.meter(DO_SEND_MESSAGE_COUNT).mark();
        metricsRegistry.histogram(DO_SEND_MESSAGE_BODY_SIZE).update(amqpMessage.getBody().length);

        super.doSendMessage(channel, amqpMessage);
    }

    @Override
    public void onClusterCreated(Cluster cluster) {
        final String queueName = cluster.getName();
        final String deadLetterExchangeName = String.format(deadLetterExchangeNameFormat, exchangeName);
        final String deadLetterQueueName = String.format(deadLetterQueueNameFormat, queueName);

        DirectExchange deadLetterExchange = new DirectExchange(deadLetterExchangeName);
        rabbitAdmin.declareExchange(deadLetterExchange);
        Queue deadLetterQueue = new Queue(deadLetterQueueName);
        rabbitAdmin.declareQueue(deadLetterQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("#"));


        final Queue queue = new Queue(
                queueName,
                queueDurable,
                false,
                false,
                ImmutableMap.<String, Object>builder()
                        .put("x-dead-letter-exchange", deadLetterExchangeName)
                        .build()
        );

        TopicExchange exchange = new TopicExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("#"));

        super.onClusterCreated(cluster);
    }
}
