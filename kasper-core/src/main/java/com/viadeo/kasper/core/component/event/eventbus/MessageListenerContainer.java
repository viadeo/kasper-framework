package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import org.apache.log4j.MDC;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageListenerContainer extends SimpleMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerContainer.class);

    private final EventListener eventListener;
    private final EventBusPolicy eventBusPolicy;

    public MessageListenerContainer(
            final EventBusPolicy eventBusPolicy,
            final ConnectionFactory connectionFactory,
            final EventListener eventListener
    ) {
        super(connectionFactory);
        this.eventBusPolicy = eventBusPolicy;
        this.eventListener = checkNotNull(eventListener);
    }

    @Override
    public void start() {
        if (eventBusPolicy == EventBusPolicy.NORMAL) {
            LOGGER.debug("starting message listener container on {}", Lists.newArrayList(getQueueNames()));
            super.start();
        } else {
            LOGGER.debug(
                    "The event bus policy do not allow to start the events consumption, <listener={}> <policy={}>",
                    eventListener.getClass().getName(), eventBusPolicy
            );
        }
    }

    @Override
    protected void executeListener(Channel channel, Message message) throws Throwable {
        MDC.clear();
        MDC.put("appRoute", eventListener.getClass().getName());

        super.executeListener(channel, message);
    }

    @Override
    public void stop() {
        LOGGER.debug("stopping message listener container on {}", Lists.newArrayList(getQueueNames()));
        super.stop();
    }

    @Override
    protected void invokeListener(Channel channel, Message message) throws Exception {
        checkNotNull(message);
        super.invokeListener(channel, new EventBusMessage(message, eventListener.getClass()));
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    @VisibleForTesting
    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }
}
