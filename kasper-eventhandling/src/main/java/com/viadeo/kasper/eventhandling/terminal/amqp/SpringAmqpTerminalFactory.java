// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.eventhandling.serializer.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.amqp.*;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPConsumerConfiguration;
import org.axonframework.serializer.Serializer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpringAmqpTerminalFactory {

    private final Config config;

    private AMQPMessageConverter messageConverter;
    private MetricRegistry metricRegistry;
    private ListenerContainerLifecycleManager listenerContainerLifecycleManager;
    private SpringAMQPConsumerConfiguration consumerConfiguration;
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private RoutingKeyResolver routingKeyResolver;

    public SpringAmqpTerminalFactory(final Config config) {
        this.config = config;
    }

    public SpringAmqpTerminalFactory with(final AMQPMessageConverter amqpMessageConverter) {
        this.messageConverter = checkNotNull(amqpMessageConverter);
        return this;
    }

    public SpringAmqpTerminalFactory with(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        return this;
    }

    public SpringAmqpTerminalFactory with(final SpringAMQPConsumerConfiguration consumerConfiguration) {
        this.consumerConfiguration = consumerConfiguration;
        return this;
    }

    public SpringAmqpTerminalFactory with(final ListenerContainerLifecycleManager listenerContainerLifecycleManager) {
        this.listenerContainerLifecycleManager = listenerContainerLifecycleManager;
        return this;
    }

    public SpringAmqpTerminalFactory with(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }


    public SpringAmqpTerminalFactory with(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public SpringAmqpTerminalFactory with(RoutingKeyResolver routingKeyResolver) {
        this.routingKeyResolver = routingKeyResolver;
        return this;
    }

    public EventBusTerminal create() {

        if (null == this.routingKeyResolver) {
            this.routingKeyResolver = new ClassRoutingKeyResolver();
        }

        if (null == this.serializer) {
            serializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());
        }

        if (null == this.connectionFactory) {
            connectionFactory = connectionFactory();
        }

        if (null == metricRegistry) {
            metricRegistry = new MetricRegistry();
        }

        if (null == consumerConfiguration) {
            consumerConfiguration = new SpringAMQPConsumerConfiguration();
            consumerConfiguration.setErrorHandler(new InstrumentedErrorHandler(new ConditionalRejectingErrorHandler(), metricRegistry));
        }

        if (null == this.messageConverter) {
            this.messageConverter = new DefaultAMQPMessageConverter(
                    serializer,
                    routingKeyResolver,
                    true
            );
        }

        if (null == listenerContainerLifecycleManager) {
            listenerContainerLifecycleManager = new ListenerContainerLifecycleManager();
            listenerContainerLifecycleManager.setDefaultConfiguration(consumerConfiguration);
            listenerContainerLifecycleManager.setConnectionFactory(connectionFactory);
            listenerContainerLifecycleManager.start();
        }

        final SpringAMQPTerminal terminal = new InstrumentedSpringAmqpTerminal(
                new RabbitAdmin(connectionFactory),
                config.getString("exchange.dead_letter.name_format"),
                config.getString("queue.dead_letter.name_format"),
                config.getString("exchange.name"),
                config.getBoolean("queue.durable"),
                metricRegistry
        );

        terminal.setConnectionFactory(connectionFactory);
        terminal.setExchangeName(config.getString("exchange.name"));
        terminal.setSerializer(serializer);
        terminal.setRoutingKeyResolver(routingKeyResolver);
        terminal.setMessageConverter(messageConverter);
        terminal.setDurable(config.getBoolean("exchange.durable"));
        terminal.setTransactional(config.getBoolean("exchange.transactional"));
        terminal.setListenerContainerLifecycleManager(listenerContainerLifecycleManager);

        return terminal;
    }


    ConnectionFactory connectionFactory() {

        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(config.getString("hostname"));
        factory.setPort(config.getInt("port"));
        factory.setUsername(config.getString("username"));
        factory.setPassword(config.getString("password"));


        return factory;
    }
}
