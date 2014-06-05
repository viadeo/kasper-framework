package com.viadeo.kasper.eventhandling.amqp;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.Channel;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterMetaData;
import org.axonframework.eventhandling.DefaultClusterMetaData;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AMQPCluster implements Cluster {

    private final Reflections reflections;
    private final RabbitAdmin admin;
    private final RabbitTemplate template;
    private final ConnectionFactory connectionFactory;
    private final ErrorHandler errorHandler;
    private final String name;
    private final String queueNameFormat;
    private final String deadLetterExchangeNameFormat;
    private final String exchangeName;
    private final String deadLetterQueueNameFormat;
    private final boolean queueDurable;
    private final Map<EventListener, SimpleMessageListenerContainer> containerMap;
    private final DefaultClusterMetaData clusterMetaData;

    protected AMQPCluster(String name,
                          RabbitAdmin admin,
                          RabbitTemplate template,
                          String exchangeName,
                          String deadLetterExchangeNameFormat,
                          String queueNameFormat,
                          String deadLetterQueueNameFormat,
                          boolean queueDurable,
                          ConnectionFactory connectionFactory,
                          ErrorHandler errorHandler
    ) {
        this.name = name;
        this.admin = admin;
        this.template = template;
        this.queueNameFormat = queueNameFormat;
        this.deadLetterExchangeNameFormat = deadLetterExchangeNameFormat;
        this.exchangeName = exchangeName;
        this.deadLetterQueueNameFormat = deadLetterQueueNameFormat;
        this.queueDurable = queueDurable;
        this.connectionFactory = connectionFactory;
        this.errorHandler = errorHandler;
        this.reflections = new Reflections();
        this.containerMap = new HashMap<>();
        this.clusterMetaData = new DefaultClusterMetaData();
    }


    /**
     * Setup and start and associate a spring amqp container to the given listener
     * This method only accept instances of kasper events because we need to get
     * the managed event class in order to process the topology.
     *
     * @param eventListener eventListener to subscribe
     */
    public void subscribe(EventListener eventListener) {

        if (!(eventListener instanceof com.viadeo.kasper.event.EventListener)) {
            throw new IllegalArgumentException("Sadly, this implementation require an instance of com.viadeo.kasper.event.EventListener");
        }

        final String queueName = setupTopology((com.viadeo.kasper.event.EventListener) eventListener);

        // set up the listener and container
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageListener(eventListener), template.getMessageConverter());
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMessageListener(adapter);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.initialize();
        container.setTaskExecutor(taskExecutor);
        container.setErrorHandler(errorHandler);
        container.start();

        this.containerMap.put(eventListener, container);
    }

    /**
     * Setup the rabbitmq topology : One queue per listener + One dead letter queue
     * <p/>
     * The queue is bound to the event fqn.
     * <p/>
     * If the listener handle an "abstract" event, then
     * we bind on both parent and child classes
     *
     * @param eventListener event listener
     * @return String created queue's name
     */
    private String setupTopology(com.viadeo.kasper.event.EventListener eventListener) {

        final String queueName = queueNameFormat
                .replace("{{exchange}}", exchangeName)
                .replace("{{cluster}}", getName())
                .replace("{{listener}}", eventListener.getClass().getName());

        final String deadLetterExchangeName = deadLetterExchangeNameFormat.replace("{{exchange}}", exchangeName);
        final String deadLetterQueueName = deadLetterQueueNameFormat.replace("{{queue}}", queueName);

        DirectExchange deadLetterExchange = new DirectExchange(deadLetterExchangeName);
        admin.declareExchange(deadLetterExchange);
        Queue deadLetterQueue = new Queue(deadLetterQueueName);
        admin.declareQueue(deadLetterQueue);


        // declare topic and queue
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

        // add bindings
        Class eventClass = eventListener.getEventClass();
        String routingKey = eventClass.equals(IEvent.class) ? "#" : eventClass.getName();

        Set subTypes = this.reflections.getSubTypesOf(eventClass);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));
        admin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(routingKey));

        for (Object type : subTypes) {
            String subRoutingKey = ((Class) type).getName();
            admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(subRoutingKey));
            admin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(subRoutingKey));
        }


        return queueName;
    }


    @Override
    public String getName() {
        return name;
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

    /**
     * Stop the associated container and remove it from the list of containers
     *
     * @param eventListener listener to unsubscribe
     * @see org.axonframework.eventhandling.Cluster#unsubscribe(org.axonframework.eventhandling.EventListener)
     */
    public void unsubscribe(EventListener eventListener) {
        SimpleMessageListenerContainer container = containerMap.get(eventListener);
        container.stop();
        containerMap.remove(eventListener);
    }

    public Set<EventListener> getMembers() {
        return containerMap.keySet();
    }

    public ClusterMetaData getMetaData() {
        return clusterMetaData;
    }

    /**
     * Weither or not one of the container is still running
     *
     * @return status
     */
    public synchronized boolean isRunning() {
        for (SimpleMessageListenerContainer container : containerMap.values()) {
            if (container.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start every configured containers
     */
    public void start() {
        for (SimpleMessageListenerContainer container : containerMap.values()) {
            if (!container.isRunning()) {
                container.start();
            }
        }
    }

    /**
     * stop every configured containers
     */
    public void stop() {
        for (SimpleMessageListenerContainer container : containerMap.values()) {
            if (container.isRunning()) {
                container.stop();
            }
        }
    }
}
