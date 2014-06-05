// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.viadeo.kasper.eventhandling.serializer.JacksonSerializer;
import org.axonframework.eventhandling.*;
import org.axonframework.serializer.Serializer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.ErrorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventBusFactory {

    private final Config config;

    private MessageConverter messageConverter;
    private MetricRegistry metricRegistry;
    private ErrorHandler errorHandler;
    private ObjectMapper objectMapper;

    /**
     * Use type safe configuration to assemble the bus
     *
     * @param config type safe configuration instance
     */
    public EventBusFactory(final Config config) {
        this.config = config;
    }

    /**
     * Setup the message converter used to map
     * axon format with spring amqp format
     *
     * @param messageConverter message converter
     * @return EventBusFactory
     */
    public EventBusFactory with(final MessageConverter messageConverter) {
        this.messageConverter = checkNotNull(messageConverter);
        return this;
    }

    /**
     * Setup the metric registry used to collect metrics
     *
     * @param metricRegistry metric registry
     * @return EventBusFactory
     */
    public EventBusFactory with(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        return this;
    }

    /**
     * Customize the serializer used to transform events
     *
     * @param objectMapper serializer instance
     * @return EventBusFactory
     */
    public EventBusFactory with(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    /**
     * Create the bus instance :
     *
     * @return clustering event bus
     */
    public EventBus create() {

        if (null == this.objectMapper) {
            objectMapper = new ObjectMapper();
        }

        if (null == metricRegistry) {
            metricRegistry = new MetricRegistry();
        }

        if (null == errorHandler) {
            errorHandler = new InstrumentedErrorHandler(new ConditionalRejectingErrorHandler(), metricRegistry);
        }

        ConnectionFactory connectionFactory = connectionFactory(config);

        if (null == this.messageConverter) {
            Serializer serializer = new JacksonSerializer(objectMapper);
            this.messageConverter = new EventMessageConverter(serializer);
        }

        final RabbitTemplate template = rabbitTemplate(connectionFactory, messageConverter);

        final RabbitAdmin admin = rabbitAdmin(connectionFactory);

        final ClusterSelector cluster = clusterSelector(config, connectionFactory, template, admin, errorHandler);

        return new ClusteringEventBus(cluster);
    }

    /**
     * Creates a rabbitmq admin instance (used to setup the topology)
     *
     * @param connectionFactory connection factory
     * @return rabbitmq instance
     */
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Creates the amqp cluster for default event handling
     * The cluster setup a "one queue per consumer" topology.
     *
     *
     * @param config            bus configuration
     * @param connectionFactory connection factory
     * @param template          template used for production
     * @param admin             admin used to setup topology
     * @param errorHandler      error handler
     * @return AMQP cluster
     */
    public CompositeClusterSelector clusterSelector(Config config,
                                                       ConnectionFactory connectionFactory,
                                                       RabbitTemplate template,
                                                       RabbitAdmin admin,
                                                       ErrorHandler errorHandler) {

        List<? extends ConfigObject> clusters = config.getObjectList("clusters");
        ArrayList<ClusterSelector> clusterSelectors = Lists.newArrayList();
        for (ConfigObject cluster : clusters) {
            Config clusterConfig = cluster
                    .toConfig()
                    .withFallback(config.getConfig("default"));

            Pattern pattern = Pattern.compile(clusterConfig.getString("pattern"));
            ClassNamePatternClusterSelector selector = new ClassNamePatternClusterSelector(pattern, new AMQPCluster(clusterConfig.getString("name"), admin,
                    template,
                    clusterConfig.getString("sub.exchange.name"),
                    clusterConfig.getString("sub.exchange.dead_letter.name_format"),
                    clusterConfig.getString("sub.queue.name_format"),
                    clusterConfig.getString("sub.queue.dead_letter.name_format"),
                    clusterConfig.getBoolean("sub.queue.durable"),
                    connectionFactory,
                    errorHandler));
            clusterSelectors.add(selector);
        }

        return new CompositeClusterSelector(clusterSelectors);
    }

    /**
     * Setup the rabbitTemplate used to produce message
     * The template has retry capability, configured with exponential back-off
     *
     * @param connectionFactory connection factory
     * @param messageConverter  message converter
     * @return rabbit template
     */
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {

        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(config.getInt("default.pub.exponentialBackOff.initialInterval"));
        backOffPolicy.setMultiplier(config.getDouble("default.pub.exponentialBackOff.multiplier"));
        backOffPolicy.setMaxInterval(config.getInt("default.pub.exponentialBackOff.maxInterval"));
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        template.setMessageConverter(messageConverter);

        return template;
    }

    /**
     * Create a caching connection factory (better reliability)
     *
     * @return connection factory
     */
    public ConnectionFactory connectionFactory(Config config) {

        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(config.getString("hostname"));
        factory.setPort(config.getInt("port"));
        factory.setUsername(config.getString("username"));
        factory.setPassword(config.getString("password"));

        return factory;
    }
}
