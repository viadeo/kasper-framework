// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.exception.KasperEventException;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.EventMessage;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;

public class EventMessageHandler implements ChannelAwareMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMessageHandler.class);

    private static final String HANDLE_MESSAGE_COUNT_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "count");
    private static final String HANDLE_MESSAGE_ERROR_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "error");
    private static final String HANDLE_MESSAGE_TIME_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "time");

    public static final int DEFAULT_MAX_ATTEMPTS = 5;
    public static final int DEFAULT_THRESHOLD_IN_HOURS = 4;

    private final MessageConverter converter;
    private final EventListener eventListener;
    private final MetricRegistry metricRegistry;
    private final MessageRecoverer messageRecoverer;
    private final Logger logger;
    private final int maxAttempts;
    private final int requeueThresholdInHours;

    public EventMessageHandler(MessageConverter converter, EventListener eventListener, MetricRegistry metricRegistry, MessageRecoverer messageRecoverer) {
        this(converter, eventListener, metricRegistry, messageRecoverer, DEFAULT_MAX_ATTEMPTS, DEFAULT_THRESHOLD_IN_HOURS);
    }

    public EventMessageHandler(
            MessageConverter converter,
            EventListener eventListener,
            MetricRegistry metricRegistry,
            MessageRecoverer messageRecoverer,
            int maxAttempts,
            int requeueThresholdInHours
    ) {
        this.converter = converter;
        this.eventListener = eventListener;
        this.metricRegistry = metricRegistry;
        this.messageRecoverer = messageRecoverer;
        this.maxAttempts = maxAttempts;
        this.requeueThresholdInHours = requeueThresholdInHours;
        this.logger = LoggerFactory.getLogger(eventListener.getName());
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        final EventMessage eventMessage;

        try {
            eventMessage = (EventMessage) converter.fromMessage(message);
        } catch (Throwable e) {
            messageRecoverer.recover(
                    message,
                    new MessageHandlerException(eventListener.getClass(), e)
            );
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        metricRegistry.counter(HANDLE_MESSAGE_COUNT_METRIC).inc();
        metricRegistry.histogram(HANDLE_MESSAGE_TIME_METRIC).update(timeTaken(eventMessage));

        MDC.setContextMap(
                Maps.transformEntries(
                        eventMessage.getMetaData(),
                        new Maps.EntryTransformer<String, Object, String>() {
                            @Override
                            public String transformEntry(String key, Object value) {
                                return String.valueOf(value);
                            }
                        }
                )
        );

        final EventResponse response = eventListener.handle(new com.viadeo.kasper.core.component.event.listener.EventMessage(eventMessage));
        final KasperReason reason = response.getReason();

        switch (response.getStatus()) {

            case SUCCESS:
            case OK:
                logger.debug("Successfully handled the event message ({}) by '{}'", eventMessage.getIdentifier(), eventListener.getClass().getSimpleName());
                channel.basicAck(deliveryTag, false);
                break;

            case ERROR:
                if (reason.getException().isPresent()) {
                    logger.warn("Failed to handle the event message ({}) by '{}' : {}", eventMessage.getIdentifier(), eventListener.getClass().getSimpleName(), reason.getMessages(), reason.getException().get());
                } else {
                    logger.warn("Failed to handle the event message ({}) by '{}' : {}", eventMessage.getIdentifier(), eventListener.getClass().getSimpleName(), reason.getMessages());
                }
                channel.basicAck(deliveryTag, false);
                break;

            case FAILURE:
                metricRegistry.counter(HANDLE_MESSAGE_ERROR_METRIC).inc();

                final Integer nbAttempt = getIncrementedNbAttempt(message);
                final DateTime time = new DateTime(message.getMessageProperties().getTimestamp());

                LOGGER.debug("Failed to attempt to handle the event message ({}) : {} time(s) by '{}'", eventMessage.getIdentifier(), nbAttempt, eventListener.getClass().getSimpleName());

                if (response.isTemporary()) {
                    if ( nbAttempt >= maxAttempts) {
                        if (DateTime.now().minusHours(requeueThresholdInHours).isBefore(time) ) {
                            LOGGER.info("Requeue the event message ({}) in the related queue of '{}'", eventMessage.getIdentifier(), eventListener.getClass().getSimpleName());
                            channel.basicNack(deliveryTag, false, true);
                            return;
                        } else {
                            messageRecoverer.recover(
                                    message,
                                    new MessageHandlerException(
                                            eventListener.getClass(),
                                            reason.getException().or(new RuntimeException("Failed to handle event message"))
                                    )
                            );
                            channel.basicNack(deliveryTag, false, false);
                            return;
                        }
                    }
                } else if (nbAttempt >= maxAttempts) {
                    messageRecoverer.recover(
                            message,
                            new MessageHandlerException(
                                    eventListener.getClass(),
                                    reason.getException().or(new RuntimeException("Failed to handle event message"))
                            )
                    );
                    channel.basicNack(deliveryTag, false, false);
                    break;
                }

                throw new MessageHandlerException(
                        eventListener.getClass(),
                        reason.getException().or(new KasperEventException(response.getReason().toString()))
                );

            default:
                messageRecoverer.recover(message,
                        new MessageHandlerException(
                                eventListener.getClass(),
                                reason.getException().or(new RuntimeException(String.format("Status not managed '%s'", response.getStatus())))
                        )
                );
                channel.basicNack(deliveryTag, false, false);
                break;
        }
    }

    private Integer getIncrementedNbAttempt(final Message message) {
        Integer nbAttempt = Optional.<Integer>fromNullable((Integer) message.getMessageProperties().getHeaders().get("X-KASPER-NB-ATTEMPT")).or(0);
        message.getMessageProperties().setHeader("X-KASPER-NB-ATTEMPT", ++nbAttempt);
        return nbAttempt;
    }

    private long timeTaken(EventMessage eventMessage) {
        return System.currentTimeMillis() - eventMessage.getTimestamp().getMillis();
    }
}
