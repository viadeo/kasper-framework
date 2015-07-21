package com.viadeo.kasper.core.component.eventbus.spring;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.eventbus.MetricConnectionListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import rabbitmq.mgmt.RabbitMgmtService;

@Configuration
@Profile(value = { "rabbitmq" } )
public class RabbitMQConfiguration {

    /**
     * Create a caching connection factory (better reliability)
     *
     * @param config the typesafe config
     * @param metricRegistry the metricRegistry
     * @return connection factory
     */
    @Bean
    public ConnectionFactory connectionFactory(Config config, MetricRegistry metricRegistry) {
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses(getAddresses(config));
        factory.setUsername(config.getString("infrastructure.rabbitmq.username"));
        factory.setPassword(config.getString("infrastructure.rabbitmq.password"));
        factory.setVirtualHost(config.getString("infrastructure.rabbitmq.virtualhost"));
        factory.addConnectionListener(new MetricConnectionListener(metricRegistry));

        return factory;
    }

    /**
     * Provide a utility service for managing the state of a RabbitMQ cluster via the RabbitMQ Management Console.
     * @param config a configuration
     * @return service
     */
    @Bean
    public RabbitMgmtService rabbitMgmtService(Config config) {
        return RabbitMgmtService.builder()
                .host(config.getString("infrastructure.rabbitmq.mgmt.hostname"))
                .port(config.getInt("infrastructure.rabbitmq.mgmt.port"))
                .credentials(
                        config.getString("infrastructure.rabbitmq.username"),
                        config.getString("infrastructure.rabbitmq.password")
                )
                .build();
    }

    /**
     * Creates a rabbitmq admin instance (used to setup the topology)
     *
     * @param config a configuration
     * @param connectionFactory connection factory
     * @return rabbitmq instance
     */
    @Bean
    public RabbitAdmin rabbitAdmin(Config config, ConnectionFactory connectionFactory) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOffPolicy(config));

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.getRabbitTemplate().setRetryTemplate(retryTemplate);

        return rabbitAdmin;
    }

    protected static BackOffPolicy backOffPolicy(Config config) {
        Config policyConfig = config.getConfig("runtime.eventbus.amqp.retry.exponentialBackOff");

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(policyConfig.getInt("initialInterval"));
        backOffPolicy.setMultiplier(policyConfig.getDouble("multiplier"));
        backOffPolicy.setMaxInterval(policyConfig.getInt("maxInterval"));
        return backOffPolicy;
    }

    protected String getAddresses(final Config config) {
        final int port = config.getInt("infrastructure.rabbitmq.port");

        final Iterable<String> addresses = Iterables.transform(
                Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(config.getString("infrastructure.rabbitmq.hosts")),
                new Function<String, String>() {
                    @Override
                    public String apply(String host) {
                        return host + ":" + port;
                    }
                }
        );

        return Joiner.on(',').join(addresses);
    }
}
