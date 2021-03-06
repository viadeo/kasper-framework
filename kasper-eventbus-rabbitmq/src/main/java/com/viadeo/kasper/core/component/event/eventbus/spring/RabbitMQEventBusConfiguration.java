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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.core.component.event.eventbus.*;
import com.viadeo.kasper.core.interceptor.tags.TagsInterceptor;
import io.github.fallwizard.rabbitmq.mgmt.RabbitMgmtService;
import org.axonframework.eventhandling.ClassNamePatternClusterSelector;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.serializer.Serializer;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
public class RabbitMQEventBusConfiguration {

    /**
     * The event bus, used to relay events between command and queries
     *
     * @param clusters the clusters list
     * @return kasper event bus
     */
    @Bean
    public KasperEventBus eventBus(List<Cluster> clusters) {
        ClassNamePatternClusterSelector selector = new ClassNamePatternClusterSelector(Pattern.compile(".*"), new CompositeCluster(clusters));
        KasperEventBus eventBus = new KasperEventBus(new MetricRegistry(), selector);

        // interceptors
        eventBus.register(new TagsInterceptor.Factory<Event,Void>());

        return eventBus;
    }


    @Configuration
    @Profile(value = { "!rabbitmq"} )
    public static class InMemoryClusterConfiguration {

        /**
         * Create an in-memory cluster implementation.
         *
         * @return in memory cluster
         */
        @Bean
        public Cluster memoryCluster() {
            return new SimpleCluster("simple");
        }
    }


    @Configuration
    @Profile(value = { "rabbitmq" } )
    public static class AmqpClusterConfiguration {

        /**
         * Setup the rabbitTemplate used to produce message
         * The template has retry capability, configured with exponential back-off
         *
         * @param config the typesafe config
         * @param connectionFactory connection factory
         * @param messageConverter  message converter
         * @return rabbit template
         */
        @Bean
        public RabbitTemplate rabbitTemplate(Config config, ConnectionFactory connectionFactory, MessageConverter messageConverter) {
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setBackOffPolicy(RabbitMQConfiguration.backOffPolicy(config));

            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setRetryTemplate(retryTemplate);
            template.setMessageConverter(messageConverter);

            return template;
        }

        @Bean
        public RabbitMQComponentInjector rabbitMQComponentInjector(AbstractApplicationContext applicationContext) {
            return new RabbitMQComponentInjector(applicationContext);
        }

        @Bean
        public AMQPComponentNameFormatter amqpComponentNameFormatter() {
            return new AMQPComponentNameFormatter();
        }

        @Bean
        public AMQPTopology amqpTopology(
                Config config,
                RabbitAdmin rabbitAdmin,
                RabbitMQComponentInjector rabbitMQComponentInjector,
                QueueFinder queueFinder,
                AMQPComponentNameFormatter amqpComponentNameFormatter
        ) {
            Config amqpConfig = config.getConfig("runtime.eventbus.amqp");

            AMQPTopology topology = new AMQPTopology(rabbitAdmin, new ReflectionRoutingKeysResolver(), queueFinder, amqpComponentNameFormatter);
            topology.setDeadLetterQueueMaxLength(amqpConfig.getInt("queue.deadLetterMaxLength"));
            topology.setQueueExpires(amqpConfig.getMilliseconds("queue.expires"));
            topology.setMessageTTL(amqpConfig.getMilliseconds("queue.messageTTL"));

            if (rabbitMQComponentInjector != null) {
                topology.addAMQPTopologyListener(rabbitMQComponentInjector);
            }

            return topology;
        }

        @Bean(name = "messageListenerContainerController")
        public MessageListenerContainerController messageListenerContainerController() {
            return new MessageListenerContainerController.NoController();
        }

        @Bean
        public EventBusPolicy eventBusPolicy() {
            return EventBusPolicy.NORMAL;
        }

        @Bean
        public AcknowledgeMode acknowledgeMode() {
            return AcknowledgeMode.AUTO;
        }

        @Bean
        public MessageListenerContainerFactory messageListenerContainerFactory(
                Config config,
                MetricRegistry metricRegistry,
                EventBusPolicy eventBusPolicy,
                RabbitAdmin rabbitAdmin,
                ConnectionFactory connectionFactory,
                RetryOperationsInterceptor retryOperationsInterceptor,
                AcknowledgeMode acknowledgeMode
        ) {
            Config amqpConfig = config.getConfig("runtime.eventbus.amqp");

            return new MessageListenerContainerFactory(
                    eventBusPolicy,
                    rabbitAdmin,
                    connectionFactory,
                    new InstrumentedErrorHandler(new ConditionalRejectingErrorHandler(), metricRegistry)
            )
                    .withPrefetchCount(amqpConfig.getInt("prefetchCount"))
                    .withAdvice(retryOperationsInterceptor)
                    .withAcknowledgeMode(acknowledgeMode);
        }

        @Bean
        public MessageListenerContainerManager messageListenerContainerManager(
                Config config,
                MetricRegistry metricRegistry,
                MessageListenerContainerFactory messageListenerContainerFactory,
                MessageConverter messageConverter,
                MessageListenerContainerController messageListenerContainerController
        ) {
            DefaultMessageListenerContainerManager containerManager = new DefaultMessageListenerContainerManager(
                    messageListenerContainerFactory,
                    metricRegistry,
                    messageConverter,
                    messageListenerContainerController
            );
            containerManager.setEnabledMessageHandling(config.getBoolean("runtime.eventbus.amqp.enableListeners"));
            return containerManager;
        }

        @Bean
        public QueueFinder amqpQueueFinder(
                ExchangeDescriptor exchangeDescriptor,
                Environment environment,
                Config config,
                RabbitMgmtService rabbitMgmtService,
                AMQPComponentNameFormatter amqpComponentNameFormatter
        ) {
            return new QueueFinder(
                    amqpComponentNameFormatter,
                    rabbitMgmtService,
                    config.getString("infrastructure.rabbitmq.virtualhost"),
                    environment,
                    amqpComponentNameFormatter.getFullExchangeName(
                            exchangeDescriptor.name,
                            exchangeDescriptor.version
                    ),
                    amqpComponentNameFormatter.getFallbackDeadLetterQueueName(
                            exchangeDescriptor.name,
                            exchangeDescriptor.version,
                            config.getString("runtime.eventbus.amqp.clusterName")
                    )
            );
        }

        @Bean
        public ExchangeDescriptor exchangeDescriptor(Config config) {
            return new ExchangeDescriptor(
                    config.getString("runtime.eventbus.amqp.exchange.name"),
                    config.getString("runtime.eventbus.amqp.exchange.version")
            );
        }

        /**
         * Amqp cluster is responsible for publishing and subscribing to
         * the amqp infrastructure.
         * It creates one queue per subscribed event listener
         *
         * @param config                          type safe config
         * @param exchangeDescriptor              the exchangeDescriptor
         * @param topology                        the amqp topology
         * @param queueFinder                        the amqp queue finder
         * @param rabbitTemplate                  the rabbit template
         * @param messageListenerContainerManager the message listener container manager
         * @param metricRegistry                  the metricRegistry
         * @return a lifecycle aware cluster
         */
        @Bean
        public AMQPCluster amqpCluster(
                Config config,
                ExchangeDescriptor exchangeDescriptor,
                AMQPTopology topology,
                QueueFinder queueFinder,
                RabbitTemplate rabbitTemplate,
                MessageListenerContainerManager messageListenerContainerManager,
                MetricRegistry metricRegistry
        ) {
            return new AMQPCluster(
                    config.getString("runtime.eventbus.amqp.clusterName"),
                    exchangeDescriptor,
                    topology,
                    rabbitTemplate,
                    messageListenerContainerManager,
                    metricRegistry,
                    queueFinder
            );
        }

        protected Serializer createEventMessageSerializer(ObjectMapper objectMapper) {
            checkNotNull(objectMapper);

            final ObjectMapper objectMapperCopy = objectMapper.copy();
            objectMapperCopy.setVisibilityChecker(objectMapperCopy.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.DEFAULT));

            return new JacksonSerializer(objectMapperCopy);
        }

        /**
         * Convert message from spring to axon/kasper format
         * @param contextHelper the contextHelper
         * @param objectMapper the objectMapper
         *
         * @return spring amqp message converter
         */
        @Bean
        public MessageConverter messageConverter(ContextHelper contextHelper, ObjectMapper objectMapper) {
            return new EventBusMessageConverter(contextHelper, createEventMessageSerializer(objectMapper));
        }

        @Bean
        public RepublishMessageRecoverer republishMessageRecoverer(
                final Config config,
                final AMQPComponentNameFormatter amqpComponentNameFormatter,
                final RabbitTemplate rabbitTemplate
        ) {
            final String exchangeName = config.getString("runtime.eventbus.amqp.exchange.name");
            final String exchangeVersion = config.getString("runtime.eventbus.amqp.exchange.version");

            return new RepublishMessageRecoverer(
                    rabbitTemplate,
                    amqpComponentNameFormatter.getDeadLetterExchangeName(exchangeName, exchangeVersion)
            );
        }

        @Bean
        public RetryOperationsInterceptor retryOperationsInterceptor(final Config config, final MessageRecoverer messageRecoverer) {
            final int maxAttempts = config.getInt("runtime.eventbus.amqp.interceptor.retry.maxAttempts");

            return RetryInterceptorBuilder.stateless()
                    .maxAttempts(maxAttempts)
                    .recoverer(messageRecoverer)
                    .build();
        }
    }
}
