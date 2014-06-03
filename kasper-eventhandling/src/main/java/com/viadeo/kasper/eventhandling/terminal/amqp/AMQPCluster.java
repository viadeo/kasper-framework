package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.rabbitmq.client.Channel;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.AbstractCluster;
import org.axonframework.eventhandling.EventListener;
import org.reflections.Reflections;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.util.ErrorHandler;

import java.util.Set;


public class AMQPCluster extends AbstractCluster {

    private final Reflections reflections;
    private RabbitAdmin admin;
    private RabbitTemplate template;
    private String deadLetterExchangeNameFormat;
    private String exchangeName;
    private String deadLetterQueueNameFormat;
    private boolean queueDurable;
    private ConnectionFactory connectionFactory;
    private ErrorHandler errorHandler;

    protected AMQPCluster(String name, RabbitAdmin admin, RabbitTemplate template, String deadLetterExchangeNameFormat, String exchangeName, String deadLetterQueueNameFormat, boolean queueDurable, ConnectionFactory connectionFactory, ErrorHandler errorHandler) {
        super(name);
        this.admin = admin;
        this.template = template;
        this.deadLetterExchangeNameFormat = deadLetterExchangeNameFormat;
        this.exchangeName = exchangeName;
        this.deadLetterQueueNameFormat = deadLetterQueueNameFormat;
        this.queueDurable = queueDurable;
        this.connectionFactory = connectionFactory;
        this.errorHandler = errorHandler;
        this.reflections = new Reflections();
    }

    @Override
    public void subscribe(EventListener eventListener) {

        if (eventListener instanceof com.viadeo.kasper.event.EventListener) {
            Class eventClass = ((com.viadeo.kasper.event.EventListener) eventListener).getEventClass();

            final String queueName = getName() + "." + eventListener.getClass().getName();
            final String deadLetterExchangeName = String.format(deadLetterExchangeNameFormat, exchangeName);
            final String deadLetterQueueName = String.format(deadLetterQueueNameFormat, queueName);

            DirectExchange deadLetterExchange = new DirectExchange(deadLetterExchangeName);
            admin.declareExchange(deadLetterExchange);
            Queue deadLetterQueue = new Queue(deadLetterQueueName);
            admin.declareQueue(deadLetterQueue);


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
            admin.declareExchange(exchange);
            admin.declareQueue(queue);

            Set subTypesOf = this.reflections.getSubTypesOf(eventClass);
            admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(eventClass.getName()));
            admin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(eventClass.getName()));
            for (Object o : subTypesOf) {
                String routingKey = ((Class) o).getName();
                admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));
                admin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(routingKey));
            }

            // set up the listener and container
            MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageListener(eventListener), template.getMessageConverter());
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
            container.setMessageListener(adapter);
            container.setQueueNames(queueName);
            container.setPrefetchCount(10);
            container.setErrorHandler(errorHandler);
            container.start();
        }

        super.subscribe(eventListener);
    }

    @Override
    public void publish(final EventMessage... events) {

        template.execute(new ChannelCallback<Object>() {
            @Override
            public Object doInRabbit(Channel channel) throws Exception {
                for (EventMessage event : events) {
                    template.convertAndSend(exchangeName, event.getPayloadType().getName(), event);
                }
                return null;
            }
        });
    }
}
