// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.core.component.event.eventbus.spring;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.event.eventbus.MetricConnectionListener;
import io.github.fallwizard.rabbitmq.mgmt.RabbitMgmtService;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

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
