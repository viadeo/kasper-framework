package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Lists;
import org.aopalliance.aop.Advice;
import org.axonframework.eventhandling.EventListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.ErrorHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageListenerContainerFactory {

    public static final int DEFAULT_PREFETCH_COUNT = 10;

    private final RabbitAdmin rabbitAdmin;
    private final ConnectionFactory connectionFactory;
    private final ErrorHandler errorHandler;
    private final List<Advice> advices;

    private int prefetchCount;
    private AcknowledgeMode acknowledgeMode;

    public MessageListenerContainerFactory(
            RabbitAdmin rabbitAdmin,
            ConnectionFactory connectionFactory,
            ErrorHandler errorHandler
    ) {
        this(rabbitAdmin, connectionFactory, errorHandler, DEFAULT_PREFETCH_COUNT);
    }

    public MessageListenerContainerFactory(
            RabbitAdmin rabbitAdmin,
            ConnectionFactory connectionFactory,
            ErrorHandler errorHandler,
            int prefetchCount
    ) {
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
        MessageListenerContainer container = new MessageListenerContainer(connectionFactory, eventListener);
        container.setMessageListener(messageListener);
        container.setQueueNames(queueName);
        container.setPrefetchCount(prefetchCount);
        container.setErrorHandler(errorHandler);
        container.setRabbitAdmin(rabbitAdmin);
        container.setAcknowledgeMode(acknowledgeMode);

        if ( ! advices.isEmpty()) {
            container.setAdviceChain(advices.toArray(new Advice[advices.size()]));
        }

        return container;
    }
}
