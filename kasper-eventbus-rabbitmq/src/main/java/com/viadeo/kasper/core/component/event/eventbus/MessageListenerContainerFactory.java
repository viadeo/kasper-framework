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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageListenerContainerFactory {

    public static final int DEFAULT_PREFETCH_COUNT = 10;

    private final RabbitAdmin rabbitAdmin;
    private final ConnectionFactory connectionFactory;
    private final ErrorHandler errorHandler;
    private final List<Advice> advices;
    private final EventBusPolicy eventBusPolicy;

    private int prefetchCount;
    private AcknowledgeMode acknowledgeMode;

    public MessageListenerContainerFactory(
            EventBusPolicy eventBusPolicy,
            RabbitAdmin rabbitAdmin,
            ConnectionFactory connectionFactory,
            ErrorHandler errorHandler
    ) {
        this(eventBusPolicy, rabbitAdmin, connectionFactory, errorHandler, DEFAULT_PREFETCH_COUNT);
    }

    public MessageListenerContainerFactory(
            EventBusPolicy eventBusPolicy,
            RabbitAdmin rabbitAdmin,
            ConnectionFactory connectionFactory,
            ErrorHandler errorHandler,
            int prefetchCount
    ) {
        this.eventBusPolicy = eventBusPolicy;
        this.rabbitAdmin = rabbitAdmin;
        this.connectionFactory = connectionFactory;
        this.errorHandler = errorHandler;
        this.prefetchCount = prefetchCount;
        this.advices = Lists.newArrayList();
        this.acknowledgeMode = AcknowledgeMode.AUTO;
    }

    /**
     * Set the {@link org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer#setPrefetchCount(int)}
     *
     * @param prefetchCount number of message to fetch out of rabbitmq in a single call
     * @return instance
     */
    public MessageListenerContainerFactory withPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
        return this;
    }

    public  MessageListenerContainerFactory withAdvice(final Advice advice) {
        advices.add(checkNotNull(advice));
        return this;
    }

    public  MessageListenerContainerFactory withAcknowledgeMode(final AcknowledgeMode acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
        return this;
    }

    public <LISTENER extends EventListener> MessageListenerContainer create(String queueName, LISTENER eventListener, MessageListener messageListener) {
        MessageListenerContainer container = new MessageListenerContainer(eventBusPolicy, connectionFactory, eventListener);
        container.setMessageListener(messageListener);
        container.setQueueNames(queueName);
        container.setPrefetchCount(prefetchCount);
        container.setErrorHandler(errorHandler);
        container.setRabbitAdmin(rabbitAdmin);
        container.setAcknowledgeMode(acknowledgeMode);
        container.setTaskExecutor(
                new SimpleAsyncTaskExecutor(
                        new ThreadFactoryBuilder()
                                .setNameFormat("consumer-" + eventListener.getHandlerClass().getSimpleName() + "-%d")
                                .build()
                )
        );

        if ( ! advices.isEmpty()) {
            container.setAdviceChain(advices.toArray(new Advice[advices.size()]));
        }

        return container;
    }
}
