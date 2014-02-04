// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal.amqp;

import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.components.eventbus.EventBusTerminalFactory;
import com.viadeo.kasper.client.platform.components.eventbus.JacksonSerializer;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.ExchangeConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.QueueConfiguration;
import com.viadeo.kasper.client.platform.components.eventbus.configuration.SpringAmqpTerminalConfiguration;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.amqp.DefaultAMQPMessageConverter;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

public class SpringAmqpTerminalFactory implements EventBusTerminalFactory {

    private final SpringAmqpTerminalConfiguration configuration;

    public SpringAmqpTerminalFactory(final SpringAmqpTerminalConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public EventBusTerminal createEventBusTerminal() {
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
}
