// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.*;
import com.viadeo.kasper.client.platform.components.eventbus.kafka.Consumer;
import com.viadeo.kasper.client.platform.components.eventbus.kafka.KafkaTerminal;
import com.viadeo.kasper.client.platform.components.eventbus.kafka.Producer;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.amqp.DefaultAMQPMessageConverter;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.eventhandling.async.*;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.NoTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.base.Optional;

import static com.viadeo.kasper.client.platform.components.eventbus.configuration.KafkaTerminalConfiguration.*;

public class KasperEventBusBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBusBuilder.class);

    private Optional<ClusterSelectorConfiguration> optionalClusterSelectorConfiguration;
    private Optional<TerminalConfiguration> optionalTerminalConfiguration;

    public KasperEventBusBuilder() {
        this(new KasperEventBusConfiguration());
    }

    public KasperEventBusBuilder(final KasperEventBusConfiguration configuration) {
        with(configuration.getClusterSelectorConfiguration());
        with(configuration.getTerminalConfiguration());
    }

    public KasperEventBusBuilder with(final ClusterSelectorConfiguration clusterSelectorConfiguration) {
        this.optionalClusterSelectorConfiguration = Optional.fromNullable(clusterSelectorConfiguration);
        return this;
    }

    public KasperEventBusBuilder with(final TerminalConfiguration terminalConfiguration) {
        this.optionalTerminalConfiguration = Optional.fromNullable(terminalConfiguration);
        return this;
    }

    public KasperEventBus build() {
        final ClusterSelector clusterSelector;
        KasperEventBus kasperEventBus = null;

        if (optionalClusterSelectorConfiguration.isPresent()) {
            clusterSelector = clusterSelector(optionalClusterSelectorConfiguration.get());
        } else {
            clusterSelector = new DefaultClusterSelector();
        }

        if (optionalTerminalConfiguration.isPresent()) {
            final TerminalConfiguration terminalConfiguration = optionalTerminalConfiguration.get();

            if (SpringAmqpTerminalConfiguration.class.isAssignableFrom(terminalConfiguration.getClass())) {
                final SpringAmqpTerminalConfiguration springAmqpTerminalConfiguration = (SpringAmqpTerminalConfiguration) terminalConfiguration;
                kasperEventBus = new KasperEventBus(clusterSelector, amqpTerminal(springAmqpTerminalConfiguration));
            } else if (KafkaTerminalConfiguration.class.isAssignableFrom(terminalConfiguration.getClass())) {
                final KafkaTerminalConfiguration kafkaTerminalConfiguration = (KafkaTerminalConfiguration) terminalConfiguration;
                kasperEventBus = new KasperEventBus(clusterSelector, kafkaTerminal(kafkaTerminalConfiguration));
            }
        }

        if (null == kasperEventBus) {
            kasperEventBus = new KasperEventBus(clusterSelector);
        }

        return kasperEventBus;
    }

    private ErrorHandler errorHandler() {
        return new DefaultErrorHandler(RetryPolicy.proceed()) {
            @Override
            public RetryPolicy handleError(final Throwable exception,
                                           final EventMessage eventMessage,
                                           final EventListener eventListener) {
                            /* TODO: store the error, generate error event */
                LOGGER.error(String.format("Error %s occured during processing of event %s in listener %s ",
                        exception.getMessage(),
                        eventMessage.getPayload().getClass().getName(),
                        eventListener.getClass().getName()
                ));
                return super.handleError(exception, eventMessage, eventListener);
            }
        };
    }

    private Cluster simpleCluster(final String name) {
        return new SimpleCluster(name);
    }

    private Cluster asynchronousCluster(final String name, final ClusterSelectorConfiguration configuration) {
        return new AsynchronousCluster(
                name,
                new ThreadPoolExecutor(
                        configuration.getPoolSize(),
                        configuration.getMaximumPoolSize(),
                        configuration.getKeepAliveTime(),
                        configuration.getTimeUnit(),
                        new LinkedBlockingQueue<Runnable>()
                ),
                new DefaultUnitOfWorkFactory(new NoTransactionManager()),
                new SequentialPolicy(),
                errorHandler()
        );
    }

    protected ClusterSelector clusterSelector(final ClusterSelectorConfiguration configuration) {
        return new DomainClusterSelector("com.viadeo.platform",
                configuration.isAsynchronous() ?
                        new Function<String, Cluster>() {
                            @Override
                            public Cluster apply(final String name) {
                                return asynchronousCluster(name, configuration);
                            }
                        }
                        :
                        new Function<String, Cluster>() {
                            @Override
                            public Cluster apply(final String name) {
                                return simpleCluster(name);
                            }
                        }
                );
    }

    private CachingConnectionFactory cachingConnectionFactory(final SpringAmqpTerminalConfiguration configuration) {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory(configuration.getHostname(), configuration.getPort());
        connectionFactory.setUsername(configuration.getUsername());
        connectionFactory.setPassword(configuration.getPassword());
        connectionFactory.setVirtualHost(configuration.getVhost());
        return connectionFactory;
    }

    private Queue queue(final QueueConfiguration configuration) {
        return new Queue(configuration.getName(), configuration.getDurable(), false, configuration.getAutoDelete());
    }

    private Exchange exchange(final ExchangeConfiguration configuration) {
        switch (configuration.getType()) {
            case ExchangeTypes.TOPIC:
                return new TopicExchange(
                        configuration.getName(),
                        configuration.getDurable(),
                        configuration.getAutoDelete()
                );
            case ExchangeTypes.DIRECT:
                return new DirectExchange(
                        configuration.getName(),
                        configuration.getDurable(),
                        configuration.getAutoDelete()
                );
            case ExchangeTypes.FANOUT:
                return new FanoutExchange(
                        configuration.getName(),
                        configuration.getDurable(),
                        configuration.getAutoDelete()
                );
            default:
                throw new KasperException("Exchange type not supported : " + configuration.getType());
        }
    }

    protected EventBusTerminal kafkaTerminal(final KafkaTerminalConfiguration configuration) {
        final String topic = configuration.getTopic();

        final ConsumerConfiguration consumerConfiguration = configuration.getConsumerConfiguration();
        final ProducerConfiguration producerConfiguration = configuration.getProducerConfiguration();

        final Properties props = new Properties();
        props.put("serializer.class", "com.viadeo.kasper.client.platform.components.eventbus.kafka.EventMessageSerializer");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("metadata.broker.list", producerConfiguration.getBrokerList());
        props.put("zookeeper.connect", consumerConfiguration.getZookeeperConnect());
        props.put("zookeeper.session.timeout.ms", consumerConfiguration.getZookeeperSessionTimeoutInMillis());
        props.put("zookeeper.sync.time.ms", consumerConfiguration.getZookeeperSyncTimeInMillis());
        props.put("group.id", consumerConfiguration.getGroupId());
        props.put("auto.commit.interval.ms", consumerConfiguration.getAutoCommitIntervalInMillis());

        final Producer producer = new Producer(props, topic);
        final Consumer consumer = new Consumer(props, topic);
        consumer.consume();

        return new KafkaTerminal(
                producer,
                consumer
        );
    }

    private EventBusTerminal amqpTerminal(final SpringAmqpTerminalConfiguration configuration) {
        final CachingConnectionFactory connectionFactory = cachingConnectionFactory(configuration);

        final JacksonSerializer serializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());

        final Exchange exchange = exchange(configuration.getExchangeConfiguration());

        final Queue queue = queue(configuration.getQueueConfiguration());

        final RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(
                new Binding(queue.getName(), Binding.DestinationType.QUEUE, exchange.getName(), "#", Maps.<String, Object>newHashMap())
        );

        final ListenerContainerLifecycleManager listenerContainerLifecycleManager = new ListenerContainerLifecycleManager();
        listenerContainerLifecycleManager.setConnectionFactory(connectionFactory);
        listenerContainerLifecycleManager.start();

        final SpringAMQPTerminal terminal = new SpringAMQPTerminal();
        terminal.setExchange(exchange);
        terminal.setConnectionFactory(connectionFactory);
        terminal.setMessageConverter(new DefaultAMQPMessageConverter(serializer));
        terminal.setListenerContainerLifecycleManager(listenerContainerLifecycleManager);

        return terminal;
    }
}
