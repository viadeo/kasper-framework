package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.saga.SagaWrapper;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultMessageListenerContainerManager implements MessageListenerContainerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageListenerContainerManager.class);

    private final MetricRegistry metricRegistry;
    private final Map<String, MessageListenerContainer> containerByClassName;
    private final Map<String, EventListener> eventListenerByClassName;
    private final MessageConverter messageConverter;
    private final MessageListenerContainerFactory messageListenerContainerFactory;
    private final MessageListenerContainerController messageListenerContainerController;

    private boolean enabledMessageHandling;

    public DefaultMessageListenerContainerManager(
            final MessageListenerContainerFactory messageListenerContainerFactory,
            final MetricRegistry metricRegistry,
            final MessageConverter messageConverter,
            final MessageListenerContainerController messageListenerContainerController) {

        this.messageListenerContainerController = checkNotNull(messageListenerContainerController);
        this.messageListenerContainerFactory = checkNotNull(messageListenerContainerFactory);
        this.messageConverter = checkNotNull(messageConverter);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.containerByClassName = Maps.newHashMap();
        this.eventListenerByClassName = Maps.newHashMap();
        this.enabledMessageHandling = false;
    }

    @Override
    public MessageListenerContainer register(EventListener eventListener, String queueName) {
        MessageListenerContainer messageListenerContainer = messageListenerContainerFactory.create(
                queueName,
                eventListener,
                new MessageListenerAdapter(
                        createDelegateMessageListener(messageConverter, eventListener, metricRegistry, enabledMessageHandling),
                        messageConverter
                )
        );

        final String classname;

        if (SagaWrapper.class.isAssignableFrom(eventListener.getClass())) {
            SagaWrapper sagaWrapper = (SagaWrapper) eventListener;
            classname = sagaWrapper.getHandlerClass().getName();
        } else {
            classname = eventListener.getClass().getName();
        }

        containerByClassName.put(classname, messageListenerContainer);
        eventListenerByClassName.put(classname, eventListener);

        return messageListenerContainer;
    }

    @Override
    public MessageListenerContainer unregister(EventListener eventListener) {
        String classname = eventListener.getClass().getName();
        eventListenerByClassName.remove(classname);

        MessageListenerContainer messageListenerContainer = containerByClassName.remove(classname);
        messageListenerContainer.stop();

        return messageListenerContainer;
    }

    @Override
    public Optional<MessageListenerContainer> get(Class<?> eventListenerClass) {
        return Optional.fromNullable(containerByClassName.get(eventListenerClass.getName()));
    }

    @Override
    public Collection<MessageListenerContainer> containers() {
        return containerByClassName.values();
    }

    @Override
    public void stopAll() {
        LOGGER.info("Stop event consumption");

        for (SimpleMessageListenerContainer container : containers()) {
            if (container.isRunning()) {
                container.stop();
            }
        }
    }

    @Override
    public void startAll() {
        LOGGER.info("Start event consumption");

        for (MessageListenerContainer container : containers()) {
            if ( ! container.isRunning() && messageListenerContainerController.canStart(container)) {
                container.start();
            }
        }
    }

    public void setEnabledMessageHandling(boolean enabledMessageHandling) {
        this.enabledMessageHandling = checkNotNull(enabledMessageHandling);
    }

    protected Object createDelegateMessageListener(
            final MessageConverter messageConverter,
            final EventListener eventListener,
            final MetricRegistry metricRegistry,
            final boolean enabledMessageHandling
    ) {
        return new MessageHandler(eventListener, metricRegistry, enabledMessageHandling);
    }

}
