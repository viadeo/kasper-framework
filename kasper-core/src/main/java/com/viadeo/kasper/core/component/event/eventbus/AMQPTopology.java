package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class AMQPTopology {

    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPTopology.class);

    public static final AMQPComponentNameFormatter DEFAULT_COMPONENT_NAME_FORMATTER = new AMQPComponentNameFormatter();
    public static final int DEFAULT_DEAD_LETTER_MAX_LENGTH = 50000;
    public static final int ONE_DAY_IN_MILLIS = 1000 * 86400;

    private final RabbitAdmin admin;
    private final RoutingKeysResolver routingKeysResolver;
    private final List<AMQPTopologyListener> amqpTopologyListeners;
    private final AMQPComponentNameFormatter componentNameFormatter;

    private Long queueExpires;
    private Long messageTTL;
    private int deadLetterQueueMaxLength;

    public AMQPTopology(RabbitAdmin admin, RoutingKeysResolver routingKeysResolver) {
        this(admin, routingKeysResolver, DEFAULT_COMPONENT_NAME_FORMATTER);
    }

    public AMQPTopology(RabbitAdmin admin, RoutingKeysResolver routingKeysResolver, AMQPComponentNameFormatter componentNameFormatter) {
        this.admin = admin;
        this.routingKeysResolver = routingKeysResolver;
        this.amqpTopologyListeners = Lists.newArrayList();
        this.deadLetterQueueMaxLength = DEFAULT_DEAD_LETTER_MAX_LENGTH;
        this.componentNameFormatter = componentNameFormatter;
    }

    //-----------------------------------------------

    /**
     * Create the dead letter fallback queue : only one dead letter
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @param clusterName the cluster name
     */
    public void createFallbackDeadLetterQueue(String exchangeName, String exchangeVersion, String clusterName) {
        final String deadLetterQueueName = componentNameFormatter.getFallbackDeadLetterQueueName(exchangeName, exchangeVersion, clusterName);
        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);
        final Map<String, Object> arguments = ImmutableMap.<String, Object>builder()
                .putAll(getArguments())
                .put("x-max-length", deadLetterQueueMaxLength)
                .build();

        Queue deadLetterQueue = new Queue(deadLetterQueueName, true, false, false, arguments);
        admin.declareQueue(deadLetterQueue);
        fireCreatedQueue(deadLetterQueue);

        Binding deadLetterBinding = new Binding(deadLetterQueueName, Binding.DestinationType.QUEUE, deadLetterExchangeName, "fallback.#", new HashMap<String, Object>());
        admin.declareBinding(deadLetterBinding);
        fireCreatedBinding(deadLetterBinding);
    }

    public void deleteFallbackDeadLetterQueue(String exchangeName, String exchangeVersion, String clusterName) {
        final String deadLetterQueueName = componentNameFormatter.getFallbackDeadLetterQueueName(exchangeName, exchangeVersion, clusterName);
        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);

        admin.deleteQueue(deadLetterQueueName);
        fireDeletedQueue(deadLetterQueueName);

        Binding deadLetterBinding = new Binding(deadLetterQueueName, Binding.DestinationType.QUEUE, deadLetterExchangeName, "fallback.#", new HashMap<String, Object>());
        admin.removeBinding(deadLetterBinding);
        fireDeletedBinding(deadLetterBinding);
    }

    //-----------------------------------------------

    /**
     * Create the main amqp exchange and the dead letter exchange.
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @return Exchange created
     */
    public Exchange createExchanges(final String exchangeName, final String exchangeVersion) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);

        final FanoutExchange exchange = new FanoutExchange(exchangeName, true, false, getArguments());
        admin.declareExchange(exchange);
        fireCreatedExchange(exchange);

        final Exchange versionedExchange = new TopicExchange(fullExchangeName, true, false, getArguments());
        admin.declareExchange(versionedExchange);
        fireCreatedExchange(versionedExchange);

        final Binding bindingExchangeToExchange = BindingBuilder.bind(versionedExchange).to(exchange);
        admin.declareBinding(bindingExchangeToExchange);
        fireCreatedBinding(bindingExchangeToExchange);
        

        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);
        final Map<String, Object> arguments = ImmutableMap.<String, Object>builder()
                .putAll(getArguments())
                .put("x-max-length", deadLetterQueueMaxLength)
                .build();

        final Exchange dlExchange = new DirectExchange(deadLetterExchangeName, true, false, arguments);
        admin.declareExchange(dlExchange);
        fireCreatedExchange(dlExchange);

        return versionedExchange;
    }

    /**
     * Delete the main amqp exchange and the dead letter exchange
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     */
    public void deleteExchanges(final String exchangeName, final String exchangeVersion) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);

        admin.deleteExchange(fullExchangeName);
        fireDeletedExchange(fullExchangeName);

        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);

        admin.deleteExchange(deadLetterExchangeName);
        fireDeletedExchange(deadLetterExchangeName);
    }

    //-----------------------------------------------

    /**
     * Create the amqp queue : one queue per listener
     *
     * The queue is bound to the event fqn.
     *
     * If the listener handle an "abstract" event, then we createQueue on both parent and child classes
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @param clusterName the cluster name
     * @param eventListener the event listener
     * @return Queue created
     */
    public Queue createQueue(
            final String exchangeName,
            final String exchangeVersion,
            final String clusterName,
            final EventListener eventListener
    ) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);
        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);
        final String queueName = componentNameFormatter.getQueueName(fullExchangeName, clusterName, eventListener);
        final boolean deprecatedEventListener = eventListener.getClass().isAnnotationPresent(Deprecated.class);

        final ImmutableMap.Builder<String, Object> properties = ImmutableMap.<String, Object>builder()
                .putAll(getArguments())
                .put("x-dead-letter-exchange", deadLetterExchangeName);

        if (null != queueExpires) {
            properties.put("x-expires", queueExpires);
        }

        if (null != messageTTL) {
            properties.put("x-message-ttl", messageTTL);
        }

        final Queue queue = new Queue(queueName, true, false, false, properties.build());
        admin.declareQueue(queue);
        fireCreatedQueue(queue);

        final RoutingKeys routingKeys = routingKeysResolver.resolve(eventListener);

        for (final RoutingKeys.RoutingKey routingKey : routingKeys.get()) {
            final Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, fullExchangeName, routingKey.getRoute(), new HashMap<String, Object>());

            if (deprecatedEventListener) {
                LOGGER.info("Unbinding queue due to deprecated event listener, <binding={}>", binding);
                try {
                    admin.removeBinding(binding);
                    fireDeletedBinding(binding);
                } catch (Throwable t) {
                    LOGGER.error("Failed to unbind queue, <binding={}>", binding);
                }
            } else {
                try {
                    admin.declareBinding(binding);
                    fireCreatedBinding(binding);
                } catch (AmqpIOException e) {
                    createExchanges(exchangeName, exchangeVersion);
                    admin.declareBinding(binding);
                    fireCreatedBinding(binding);
                }
            }
        }

        return queue;
    }

    /**
     * Create the dead letter amqp queue : one dead letter queue per listener
     *
     * The queue is bound to the event fqn.
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @param clusterName the cluster name
     * @param eventListener the event listener
     * @return Queue created
     */
    public Queue createDeadLetterQueue(
            final String exchangeName,
            final String exchangeVersion,
            final String clusterName,
            final EventListener eventListener
    ) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);
        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);
        final String deadLetterQueueName = componentNameFormatter.getDeadLetterQueueName(fullExchangeName, clusterName, eventListener);

        final Map<String, Object> arguments = ImmutableMap.<String, Object>builder()
                .putAll(getArguments())
                .put("x-max-length", deadLetterQueueMaxLength)
                .build();

        Queue deadLetterQueue = new Queue(deadLetterQueueName, true, false, false, arguments);
        admin.declareQueue(deadLetterQueue);
        fireCreatedQueue(deadLetterQueue);

        Binding deadLetterBinding = new Binding(deadLetterQueueName, Binding.DestinationType.QUEUE, deadLetterExchangeName, eventListener.getClass().getName(), new HashMap<String, Object>());
        admin.declareBinding(deadLetterBinding);
        fireCreatedBinding(deadLetterBinding);

        return deadLetterQueue;
    }

    /**
     * Delete the amqp queue : one queue per listener
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @param clusterName the cluster name
     * @param eventListener the event listener
     */
    public void deleteQueue(
            final String exchangeName,
            final String exchangeVersion,
            final String clusterName,
            final EventListener eventListener
    ) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);
        final String queueName = componentNameFormatter.getQueueName(fullExchangeName, clusterName, eventListener);

        admin.deleteQueue(queueName);
        fireDeletedQueue(queueName);

        for (final RoutingKeys.RoutingKey routingKey : routingKeysResolver.resolve(eventListener).all()) {
            final Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey.getRoute(), new HashMap<String, Object>());
            admin.removeBinding(binding);
            fireDeletedBinding(binding);
        }
    }

    /**
     * Delete the dead letter amqp queue : one dead letter queue per listener
     *
     * @param exchangeName the exchange name
     * @param exchangeVersion the exchange version
     * @param clusterName the cluster name
     * @param eventListener the event listener
     */
    public void deleteDeadLetterQueue(
            final String exchangeName,
            final String exchangeVersion,
            final String clusterName,
            final EventListener eventListener
    ) {
        final String fullExchangeName = componentNameFormatter.getFullExchangeName(exchangeName, exchangeVersion);
        final String queueName = componentNameFormatter.getQueueName(fullExchangeName, clusterName, eventListener);
        final String dlQueueName = componentNameFormatter.getQueueName(fullExchangeName, clusterName, eventListener);
        final String deadLetterExchangeName = componentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion);

        admin.deleteQueue(dlQueueName);
        fireDeletedQueue(dlQueueName);

        final Binding deadLetterBinding = new Binding(dlQueueName, Binding.DestinationType.QUEUE, deadLetterExchangeName, queueName, new HashMap<String, Object>());
        admin.removeBinding(deadLetterBinding);
        fireDeletedBinding(deadLetterBinding);
    }

    /**
     * Unbind the amqp queue
     *
     * @param binding the binding to remove
     */
    public void unbindQueue(Binding binding) {
        admin.removeBinding(binding);
    }

    //-----------------------------------------------

    private Map<String, Object> getArguments() {
        return ImmutableMap.<String, Object>builder()
                .put("creation-date", DateTime.now().toString())
                .build();
    }

    //-----------------------------------------------

    private void fireCreatedQueue(Queue queue) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onQueueCreated(queue);
        }
    }

    private void fireDeletedQueue(String queueName) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onQueueDeleted(queueName);
        }
    }

    private void fireCreatedExchange(Exchange exchange) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onExchangeCreated(exchange);
        }
    }

    private void fireDeletedExchange(String exchangeName) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onExchangeDeleted(exchangeName);
        }
    }

    private void fireCreatedBinding(Binding binding) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onBindingCreated(binding);
        }
    }

    private void fireDeletedBinding(Binding binding) {
        for (AMQPTopologyListener listener : amqpTopologyListeners) {
            listener.onBindingDeleted(binding);
        }
    }

    //-----------------------------------------------

    /**
     * Set the queue expires
     *
     * @param queueExpires time in millisecond before queue expiration
     * @see <a href="http://www.rabbitmq.com/ttl.html#queue-ttl">http://www.rabbitmq.com/ttl.html#queue-ttl</a>
     */
    public void setQueueExpires(Long queueExpires) {
        checkArgument(queueExpires > ONE_DAY_IN_MILLIS, "At least 1 day is required for queue expiration");
        this.queueExpires = queueExpires;
    }

    /**
     * Set the message ttl for the queues
     *
     * @param messageTTL time in milliseconds before message expiration
     */
    public void setMessageTTL(Long messageTTL) {
        checkArgument(messageTTL > ONE_DAY_IN_MILLIS, "At least 1 day is required for message expiration");
        this.messageTTL = messageTTL;
    }

    /**
     * Add a topology listener
     *
     * @param amqpTopologyListener the listener
     */
    public void addAMQPTopologyListener(AMQPTopologyListener amqpTopologyListener) {
        amqpTopologyListeners.add(checkNotNull(amqpTopologyListener));
    }

    /**
     * Set the maximum length for the dead letter queue
     * @param deadLetterQueueMaxLength the maximum size of the queue
     */
    public void setDeadLetterQueueMaxLength(int deadLetterQueueMaxLength) {
        checkArgument(deadLetterQueueMaxLength > 1, "the maximum length for the dead letter must be higher than 1");
        this.deadLetterQueueMaxLength = deadLetterQueueMaxLength;
    }

    /**
     * @return the component name formatter currently used by this topology
     */
    public AMQPComponentNameFormatter getComponentNameFormatter() {
        return componentNameFormatter;
    }
}
