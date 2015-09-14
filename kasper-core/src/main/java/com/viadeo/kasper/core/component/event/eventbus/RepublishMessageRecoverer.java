package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * forked from <code>org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer</code>
 */
public class RepublishMessageRecoverer implements MessageRecoverer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepublishMessageRecoverer.class);

    private final AmqpTemplate errorTemplate;
    private final String exchangeName;

    public RepublishMessageRecoverer(AmqpTemplate errorTemplate, String exchangeName) {
        this.errorTemplate = checkNotNull(errorTemplate);
        this.exchangeName = checkNotNull(exchangeName);
    }

    @Override
    public void recover(Message message, Throwable throwable) {

        final MessageDescriptor descriptor = new MessageDescriptor(
                message,
                throwable
        );

        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put("x-exception-stacktrace", getStackTraceAsString(descriptor.getCause()));
        headers.put("x-exception-message", descriptor.getCause().getMessage());
        headers.put("x-original-exchange", message.getMessageProperties().getReceivedExchange());
        headers.put("x-original-routingKey", message.getMessageProperties().getReceivedRoutingKey());
        headers.put("x-exception-source", descriptor.getSourceName());

        errorTemplate.send(exchangeName, descriptor.getRoutingKey(), message);

        final Object event = headers.get(EventBusMessageConverter.PAYLOAD_TYPE_KEY);
        LOGGER.error(
                "{} failed to handle {}. Republishing message to exchange '{}' with routing key '{}', <source={}> <event={}>, <stacktrace={}>",
                getClassNameFromCanonicalName(descriptor.getRoutingKey()),
                getClassNameFromCanonicalName(event),
                exchangeName,
                descriptor.getRoutingKey(),
                descriptor.getSourceName(),
                event,
                getStackTraceAsString(descriptor.getCause())
        );
    }

    static String getClassNameFromCanonicalName(final Object canonicalName) {
        try {
            final String[] split = canonicalName.toString().split("\\.");
            return split[split.length - 1];
        } catch (Exception e) {
            return "unknown";
        }
    }

    protected static String getStackTraceAsString(Throwable cause) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        cause.printStackTrace(printWriter);
        return stringWriter.getBuffer().toString();
    }

    private static class MessageDescriptor {

        private final Message message;
        private final Throwable cause;
        private final Optional<Class<EventListener>> source;
        private final String routingKey;

        private MessageDescriptor(Message message, Throwable throwable) {
            this.message = checkNotNull(message);

            if (throwable.getCause() instanceof WithSource) {
                cause= throwable.getCause().getCause();
                source = ((WithSource) throwable.getCause()).getSource();
            } else {
                cause = throwable;
                source = Optional.absent();
            }

            if (message instanceof EventBusMessage) {
                EventBusMessage eventMessage = (EventBusMessage) message;
                routingKey = eventMessage.getEventListenerClass().getName();
            } else {
                routingKey = source.isPresent() ? source.get().getName() : "fallback." + message.getMessageProperties().getReceivedRoutingKey();
            }
        }

        private Throwable getCause() {
            return cause;
        }

        public boolean hasSource() {
            return source.isPresent();
        }

        public String getSourceName() {
            return source.isPresent() ? source.get().getName() : "unknown";
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }
}
