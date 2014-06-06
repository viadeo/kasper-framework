package com.viadeo.kasper.eventhandling.amqp;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.Channel;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterMetaData;
import org.axonframework.eventhandling.DefaultClusterMetaData;
import org.axonframework.eventhandling.EventListener;
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
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class AMQPCluster implements Cluster, SmartLifecycle {

    private final RabbitAdmin admin;
    private final RabbitTemplate template;
    private final ConnectionFactory connectionFactory;
    private final ErrorHandler errorHandler;
    private final RoutingKeysResolver routingKeysResolver;
    private final String name;

    private String queueNameFormat;
    private String deadLetterExchangeNameFormat;
    private String exchangeName;
    private String deadLetterQueueNameFormat;
    private int maxPoolSize = 10;
    private int prefetchCount = 10;
    private boolean queueDurable = true;

    private final Map<EventListener, SimpleMessageListenerContainer> containerMap;
    private final DefaultClusterMetaData clusterMetaData;


    public AMQPCluster(String name,
                       RabbitAdmin admin,
                       RabbitTemplate template,
                       RoutingKeysResolver routingKeysResolver,
                       ConnectionFactory connectionFactory,
                       ErrorHandler errorHandler
    ) {
        this.name = name;
        this.admin = admin;
        this.template = template;
        this.routingKeysResolver = routingKeysResolver;
        this.connectionFactory = connectionFactory;
        this.errorHandler = errorHandler;
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
    @Override
    public void subscribe(EventListener eventListener) {

        final String queueName = setupTopology((com.viadeo.kasper.event.EventListener) eventListener);

        // set up the listener and container
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageListener(eventListener), template.getMessageConverter());
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setMessageListener(adapter);
        container.setQueueNames(queueName);
        container.setPrefetchCount(prefetchCount);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxPoolSize);
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
    protected String setupTopology(com.viadeo.kasper.event.EventListener eventListener) {

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
        List<String> routingKeys = routingKeysResolver.resolve(eventListener);
        for (String key : routingKeys) {
            admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(key));
            admin.declareBinding(BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(key));
        }

        return queueName;
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Publish messages using provided rabbit template
     *
     * @see org.springframework.amqp.rabbit.core.RabbitTemplate#execute(org.springframework.amqp.rabbit.core.ChannelCallback)
     * @param events a list of message to publish
     */
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
    @Override
    public void unsubscribe(EventListener eventListener) {
        SimpleMessageListenerContainer container = containerMap.get(eventListener);
        container.stop();
        containerMap.remove(eventListener);
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getMembers()
     */
    @Override
    public Set<EventListener> getMembers() {
        return containerMap.keySet();
    }

    /**
     * @see org.axonframework.eventhandling.Cluster#getMetaData()
     */
    @Override
    public ClusterMetaData getMetaData() {
        return clusterMetaData;
    }

    /**
     * returns true if one of the container is still running
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


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    /**
     * Set the name format used to create the queues
     * this format accept 3 placeholders :
     * - {{cluster}}
     * - {{exchange}}
     * - {{listener}}
     *
     * @param queueNameFormat the queue name format
     */
    public void setQueueNameFormat(String queueNameFormat) {
        this.queueNameFormat = checkNotNull(queueNameFormat);
    }

    /**
     * Set the dead letter exchange name format
     * this format accept 1 placeholder :
     * - {{queue}}
     *
     * @param deadLetterExchangeNameFormat the dead-letter exchange name format
     */
    public void setDeadLetterExchangeNameFormat(String deadLetterExchangeNameFormat) {
        this.deadLetterExchangeNameFormat = checkNotNull(deadLetterExchangeNameFormat);
    }

    /**
     * Set the exchange name
     *
     * @param exchangeName exchange name
     */
    public void setExchangeName(String exchangeName) {
        this.exchangeName = checkNotNull(exchangeName);
    }

    /**
     * Set the dead letter queue name format
     * this format accept 1 placeholder :
     * - {{queue}}
     *
     * @param deadLetterQueueNameFormat the dead-letter queue name format
     */
    public void setDeadLetterQueueNameFormat(String deadLetterQueueNameFormat) {
        this.deadLetterQueueNameFormat = checkNotNull(deadLetterQueueNameFormat);
    }

    /**
     * Set the {@link org.springframework.amqp.core.Queue#isDurable()}
     *
     * @param queueDurable is queue durable or not
     */
    public void setQueueDurable(boolean queueDurable) {
        this.queueDurable = checkNotNull(queueDurable);
    }

    /**
     * Set the {@link org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor#setMaxPoolSize(int))}
     *
     * @param maxPoolSize max pool size
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Set the {@link org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer#setPrefetchCount(int)}
     *
     * @param prefetchCount number of message to fetch out of rabbitmq in a single call
     */
    public void setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
    }
}
