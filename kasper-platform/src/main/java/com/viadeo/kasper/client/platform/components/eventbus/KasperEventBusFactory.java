// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import com.sun.javaws.exceptions.InvalidArgumentException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.MetaData;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.DefaultErrorHandler;
import org.axonframework.eventhandling.async.RetryPolicy;
import org.axonframework.eventhandling.async.SequentialPolicy;
import org.axonframework.serializer.Serializer;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperEventBusFactory {

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
    public KasperEventBusFactory(final Config config) {
        this.config = config;
    }

    /**
     * Setup the message converter used to map
     * axon format with spring amqp format
     *
     * @param messageConverter message converter
     * @return KasperEventBusFactory
     */
    public KasperEventBusFactory with(final MessageConverter messageConverter) {
        this.messageConverter = checkNotNull(messageConverter);
        return this;
    }

    /**
     * Setup the metric registry used to collect metrics
     *
     * @param metricRegistry metric registry
     * @return KasperEventBusFactory
     */
    public KasperEventBusFactory with(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        return this;
    }

    /**
     * Customize the serializer used to transform events
     *
     * @param objectMapper serializer instance
     * @return KasperEventBusFactory
     */
    public KasperEventBusFactory with(ObjectMapper objectMapper) {
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
            final SimpleModule module = new SimpleModule();
            module.addDeserializer(MetaData.class, new KasperMetaDataDeserializer());

            objectMapper.registerModule(module);
            Serializer serializer = new JacksonSerializer(objectMapper);
            this.messageConverter = new KasperEventMessageConverter(serializer);
        }

        final RabbitTemplate template = rabbitTemplate(connectionFactory, messageConverter);
        final RabbitAdmin admin = rabbitAdmin(connectionFactory);
        final ClusterSelector clusterSelector = clusterSelector(config, connectionFactory, template, admin, errorHandler);

        return new KasperEventBus(clusterSelector);
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
     * Get a cluster selector instance.
     * For the moment we only return one Composite cluster in order
     * to ease migration between asynchronous and amqp cluster
     *
     * @param config            bus configuration
     * @param connectionFactory connection factory
     * @param template          template used for production
     * @param admin             admin used to setup topology
     * @param errorHandler      error handler
     * @return AMQP cluster
     */
    public ClusterSelector clusterSelector(Config config,
                                                    ConnectionFactory connectionFactory,
                                                    RabbitTemplate template,
                                                    RabbitAdmin admin,
                                                    ErrorHandler errorHandler) {

        List<? extends ConfigObject> clusters = config.getObjectList("clusters");
        ArrayList<Cluster> clusterList = Lists.newArrayList();
        for (ConfigObject clusterConfigObject : clusters) {
            Config clusterConfig = clusterConfigObject
                    .toConfig()
                    .withFallback(config.getConfig("default"));

            String type = clusterConfig.getString("type");
            Cluster cluster;
            switch (type) {
                case "amqp":
                    cluster = amqpCluster(clusterConfig, connectionFactory, template, admin, errorHandler);
                    break;
                case "async":
                    cluster = asyncCluster(clusterConfig);
                    break;
                default:
                    throw new IllegalArgumentException("unknown cluster type");
            }
            clusterList.add(cluster);
        }

        Cluster cluster = new CompositeCluster(clusterList);
        return new ClassNamePatternClusterSelector(Pattern.compile(".*"), cluster);
    }

    /**
     * Retrieve an async cluster instance
     *
     * @param config bus configuration
     * @return lifecycle cluster
     */
    private Cluster asyncCluster(Config config) {

        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                config.getInt("sub.corePoolSize"),
                config.getInt("sub.maximumPoolSize"),
                config.getMilliseconds("sub.keepAliveTime"),
                TimeUnit.MILLISECONDS,
                workQueue
        );

        return new AsynchronousCluster(
                config.getString("name"),
                threadPoolExecutor,
                new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                new SequentialPolicy(),
                new AsynchronousCluster.ErrorHandler(RetryPolicy.proceed())
        );
    }

    /**
     * Retrieve the amqp cluster instance
     *
     * @param config            bus configuration
     * @param connectionFactory connection factory
     * @param template          template used for production
     * @param admin             admin used to setup topology
     * @param errorHandler      error handler
     * @return lifecycle cluster
     */
    private SmartlifeCycleCluster amqpCluster(Config config, ConnectionFactory connectionFactory, RabbitTemplate template, RabbitAdmin admin, ErrorHandler errorHandler) {

        AMQPCluster cluster = new AMQPCluster(config.getString("name"), admin,
                template,
                new KasperRoutingKeysResolver(),
                connectionFactory,
                errorHandler);

        cluster.setExchangeName(config.getString("sub.exchange.name"));
        cluster.setDeadLetterExchangeNameFormat(config.getString("sub.exchange.dead_letter.name_format"));
        cluster.setQueueNameFormat(config.getString("sub.queue.name_format"));
        cluster.setDeadLetterQueueNameFormat(config.getString("sub.queue.dead_letter.name_format"));
        cluster.setQueueDurable(config.getBoolean("sub.queue.durable"));
        cluster.setPrefetchCount(config.getInt("sub.container.prefetchCount"));
        cluster.setMaxPoolSize(config.getInt("sub.container.maxPoolSize"));

        return cluster;
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