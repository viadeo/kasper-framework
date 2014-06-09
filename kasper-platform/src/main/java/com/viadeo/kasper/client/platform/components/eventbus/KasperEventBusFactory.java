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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.axonframework.domain.MetaData;
import org.axonframework.eventhandling.ClassNamePatternClusterSelector;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.EventBus;
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
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.ArrayList;
import java.util.Set;
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
    private ConnectionFactory connectionFactory;
    private ArrayList<SmartlifeCycleCluster> clusterList;

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

        if (null == metricRegistry) {
            metricRegistry = new MetricRegistry();
        }

        final ClusterSelector clusterSelector = clusterSelector(config);

        // TODO : this is clearly the job of a DI framework
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                for (SmartlifeCycleCluster cluster : clusterList) {
                    cluster.stop();
                }
            }
        }));

        return new KasperEventBus(clusterSelector);
    }

    /**
     * Creates a rabbitmq admin instance (used to setup the topology)
     *
     * @param connectionFactory connection factory
     * @return rabbitmq instance
     */
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Get a cluster selector instance.
     * For the moment we only return one Composite cluster in order
     * to ease migration between asynchronous and amqp cluster
     *
     * @param config            bus configuration
     * @return AMQP cluster
     */
    ClusterSelector clusterSelector(Config config) {

        ConfigObject clusters = config.getObject("clusters");
        Set<String> names = clusters.keySet();
        clusterList = Lists.newArrayList();
        for (String name : names) {
            Config clusterConfig = clusters.toConfig().getConfig(name);
            String type = clusterConfig.getString("type");
            SmartlifeCycleCluster cluster;
            switch (type) {
                case "amqp":
                    cluster = amqpCluster(name, clusterConfig.withFallback(config.getConfig("defaults.amqp")));
                    break;
                case "async":
                    cluster = asyncCluster(name, clusterConfig.withFallback(config.getConfig("defaults.async")));
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
    SmartlifeCycleCluster asyncCluster(String name, Config config) {

        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                config.getInt("corePoolSize"),
                config.getInt("maximumPoolSize"),
                config.getMilliseconds("keepAliveTime"),
                TimeUnit.MILLISECONDS,
                workQueue
        );

        return new AsynchronousCluster(
                name,
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
     * @return lifecycle cluster
     */
    SmartlifeCycleCluster amqpCluster(String name, Config config) {

        if (null == connectionFactory) {
            connectionFactory = connectionFactory(config);
        }

        if (null == messageConverter) {
            messageConverter = messageConverter();
        }

        if (null == this.objectMapper) {
            objectMapper = new ObjectMapper();
        }

        if (null == errorHandler) {
            errorHandler = new InstrumentedErrorHandler(new ConditionalRejectingErrorHandler(), metricRegistry);
        }

        final RabbitTemplate template = rabbitTemplate(config, connectionFactory, messageConverter);

        final RabbitAdmin admin = rabbitAdmin(connectionFactory);

        ConcurrentTaskExecutor taskExecutor = taskExecutor(config);

        AMQPCluster cluster = new AMQPCluster(name, admin,
                template,
                new KasperRoutingKeysResolver(),
                connectionFactory,
                errorHandler,
                metricRegistry,
                taskExecutor);

        cluster.setExchangeName(config.getString("exchange.name"));
        cluster.setDeadLetterExchangeNameFormat(config.getString("exchange.deadLetterNameFormat"));
        cluster.setQueueNameFormat(config.getString("queue.nameFormat"));
        cluster.setDeadLetterQueueNameFormat(config.getString("queue.deadLetterNameFormat"));
        cluster.setQueueDurable(config.getBoolean("queue.durable"));
        cluster.setPrefetchCount(config.getInt("prefetchCount"));

        return cluster;
    }

    /**
     * Get the task executor used by containers
     *
     * @param config config for task executor
     * @return
     */
    private ConcurrentTaskExecutor taskExecutor(Config config) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(config.getInt("maximumPoolSize"));
        taskExecutor.setCorePoolSize(config.getInt("corePoolSize"));
        taskExecutor.initialize();

        return new ConcurrentTaskExecutor(taskExecutor);
    }

    /**
     * Setup the rabbitTemplate used to produce message
     * The template has retry capability, configured with exponential back-off
     *
     * @param connectionFactory connection factory
     * @param messageConverter  message converter
     * @return rabbit template
     */
    RabbitTemplate rabbitTemplate(Config config, ConnectionFactory connectionFactory, MessageConverter messageConverter) {

        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(config.getInt("retry.exponentialBackOff.initialInterval"));
        backOffPolicy.setMultiplier(config.getDouble("retry.exponentialBackOff.multiplier"));
        backOffPolicy.setMaxInterval(config.getInt("retry.exponentialBackOff.maxInterval"));
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
    ConnectionFactory connectionFactory(Config config) {

        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(config.getString("hostname"));
        factory.setPort(config.getInt("port"));
        factory.setUsername(config.getString("username"));
        factory.setPassword(config.getString("password"));

        return factory;
    }

    /**
     * Convert message from spring to axon/kasper format
     *
     * @return spring amqp message converter
     */
    MessageConverter messageConverter() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(MetaData.class, new KasperMetaDataDeserializer());

        objectMapper.registerModule(module);
        Serializer serializer = new JacksonSerializer(objectMapper);
        return new KasperEventMessageConverter(serializer);
    }
}