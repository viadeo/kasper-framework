package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.*;
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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class KasperEventBusFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBusFactory.class);

    private final KasperEventBusConfiguration configuration;

    public KasperEventBusFactory() {
        this(new KasperEventBusConfiguration());
    }

    public KasperEventBusFactory(final KasperEventBusConfiguration configuration) {
        this.configuration = configuration;
    }

    public KasperEventBus build(){
        final ClusterSelectorConfiguration clusterSelectorConfiguration = configuration.getClusterSelectorConfiguration();
        final TerminalConfiguration terminalConfiguration = configuration.getTerminalConfiguration();

        final KasperEventBus kasperEventBus;
        if(null == terminalConfiguration && null == clusterSelectorConfiguration) {
            kasperEventBus = new KasperEventBus(new DefaultClusterSelector());
        } else if(null == terminalConfiguration){
            kasperEventBus = new KasperEventBus(clusterSelector(clusterSelectorConfiguration));
        } else if(null == clusterSelectorConfiguration){
            kasperEventBus = new KasperEventBus(new DefaultClusterSelector(), terminal(terminalConfiguration));
        } else {
            kasperEventBus = new KasperEventBus(clusterSelector(clusterSelectorConfiguration), terminal(terminalConfiguration));
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

    private Cluster simpleCluster(final ClusterSelectorConfiguration configuration) {
        return new SimpleCluster(configuration.getName());
    }

    private Cluster asynchronousCluster(final ClusterSelectorConfiguration configuration) {
        return new AsynchronousCluster(
                configuration.getName(),
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

    private ClusterSelector clusterSelector(final ClusterSelectorConfiguration configuration) {
        return new DefaultClusterSelector(
                configuration.isAsynchronous() ? asynchronousCluster(configuration) : simpleCluster(configuration)
        );
    }

    private CachingConnectionFactory cachingConnectionFactory(final TerminalConfiguration configuration) {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory(configuration.getHostname(), configuration.getPort());
        connectionFactory.setUsername(configuration.getUsername());
        connectionFactory.setPassword(configuration.getPassword());
        connectionFactory.setVirtualHost(configuration.getVhost());
        return connectionFactory;
    }

    private Queue queue(final QueueConfiguration configuration){
        return new Queue(configuration.getName(), configuration.getDurable(), false, configuration.getAutoDelete());
    }

    private Exchange exchange(final ExchangeConfiguration configuration){
        switch (configuration.getType()){
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

    private EventBusTerminal terminal(final TerminalConfiguration configuration) {
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
