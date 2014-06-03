// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.eventhandling.cluster.ClassnameDynamicClusterSelector;
import com.viadeo.kasper.eventhandling.cluster.ClusterFactory;
import com.viadeo.kasper.eventhandling.serializer.JacksonSerializer;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.serializer.Serializer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.util.ErrorHandler;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventBusFactory {

    private final Config config;

    private MessageConverter messageConverter;
    private MetricRegistry metricRegistry;
    private ConnectionFactory connectionFactory;
    private Serializer serializer;
    private ErrorHandler errorHandler;

    public EventBusFactory(final Config config) {
        this.config = config;
    }

    public EventBusFactory with(final MessageConverter amqpMessageConverter) {
        this.messageConverter = checkNotNull(amqpMessageConverter);
        return this;
    }

    public EventBusFactory with(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        return this;
    }

    public EventBusFactory with(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    public EventBusFactory with(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public EventBus create() {

        if (null == this.serializer) {
            serializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());
        }

        if (null == this.connectionFactory) {
            connectionFactory = connectionFactory();
        }

        if (null == metricRegistry) {
            metricRegistry = new MetricRegistry();
        }

        if (null == errorHandler) {
            errorHandler = new InstrumentedErrorHandler(new ConditionalRejectingErrorHandler(), metricRegistry);
        }

        if (null == this.messageConverter) {
            this.messageConverter = new EventMessageConverter(serializer);
        }


        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        final RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        template.setMessageConverter(messageConverter);

        return new ClusteringEventBus(
                new ClassnameDynamicClusterSelector(config.getString("prefix"), new ClusterFactory() {
                    @Override
                    public Cluster create(final String name) {
                        return new AMQPCluster(name,
                                admin,
                                template,
                                config.getString("exchange.dead_letter.name_format"),
                                config.getString("exchange.name"),
                                config.getString("queue.dead_letter.name_format"),
                                config.getBoolean("queue.durable"),
                                connectionFactory,
                                errorHandler
                        );
                    }
                })
        );
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
