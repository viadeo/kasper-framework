// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericDomainEventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.io.EventMessageType;
import org.axonframework.serializer.MessageSerializer;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.Serializer;
import org.axonframework.serializer.SimpleSerializedObject;
import org.joda.time.DateTime;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Date;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventBusMessageConverter implements MessageConverter {

    private static final String HEADER_REQUIRED_MESSAGE = "header %s is required";

    protected static final String SERIALIZER_VERSION_KEY = "X-SERIALIZER-VERSION";
    protected static final String AGGREGATE_ID_KEY = "X-AGGREGATE-ID";
    protected static final String SEQUENCE_NUMBER_KEY = "X-SEQUENCE-NUMBER";

    protected static final String PAYLOAD_REVISION_KEY = "X-PAYLOAD-REVISION";
    protected static final String PAYLOAD_TYPE_KEY = "X-PAYLOAD-TYPE";

    protected static final String EVENT_TYPE_KEY = "X-EVENT-TYPE";
    protected static final String EVENT_TIMESTAMP_KEY = "X-EVENT-TIMESTAMP";
    protected static final String EVENT_UNIT_OF_WORK_ID = "X-EVENT-UNIT-OF-WORK-ID";
    protected static final String EVENT_PERSISTENCY_TYPE = "X-EVENT-PERSISTENCY-TYPE";

    protected static final String PREFIX_METADATA_KEY = "X-META-";
    protected static final String PREFIX_CONTEXT_KEY = "X-CONTEXT-";
    public static final int MAX_PAYLOAD_SIZE = 128 * 1000;
    public static final String MAX_PAYLOAD_SIZE_MESSAGE = "The message payload exceed allowed limit of %s, event %s has the following size %s";


    private final MessageSerializer serializer;
    private final ContextHelper contextHelper;

    /**
     * Constructor
     *
     * @param contextHelper the contextHelper
     * @param serializer message serializer
     */
    public EventBusMessageConverter(final ContextHelper contextHelper, final Serializer serializer) {
        this.serializer = new MessageSerializer(checkNotNull(serializer));
        this.contextHelper = checkNotNull(contextHelper);
    }

    /**
     * Utility method to check headers when deserializing
     *
     * @param headers headers
     * @param headerName header to check
     * @return header
     */
    private Object checkAndGetHeader(final Map<String, Object> headers, final String headerName) {
        return checkNotNull(headers.get(headerName), HEADER_REQUIRED_MESSAGE, headerName);
    }

    /**
     * Transform rabbitmq message properties to axon metadata
     *
     * @param properties message properties
     * @return axon metadata
     */
    protected Map<String, Object> toMetadata(final MessageProperties properties) {
        checkNotNull(properties);

        final Map<String, String> contextMap = Maps.newHashMap();
        final Map<String, Object> metadata = Maps.newHashMap();

        if (properties.getHeaders() != null) {
            for (final Map.Entry<String, Object> header : properties.getHeaders().entrySet()) {
                final Object value = header.getValue();
                if (value != null) {
                    if (header.getKey().startsWith(PREFIX_CONTEXT_KEY)) {
                        contextMap.put(header.getKey().substring(PREFIX_CONTEXT_KEY.length()), String.valueOf(value));
                    } else {
                        metadata.put(header.getKey(), value);
                    }
                }
            }
        }

        metadata.put("delivery-mode", MessageDeliveryMode.toInt(properties.getDeliveryMode()));
        metadata.put("message-id", properties.getMessageId());
        metadata.put("content-encoding", properties.getContentEncoding());
        metadata.put("content-type", properties.getContentType());
        metadata.put("type", properties.getType());
        metadata.put(Context.METANAME, contextHelper.createFrom(contextMap));

        return metadata;
    }

    /**
     * Transform an axon event message to an amqp message
     *
     * @param object message to convert
     * @param messageProperties spring message properties
     * @return converted message
     * @throws org.springframework.amqp.support.converter.MessageConversionException throws a MessageConversionException
     */
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        EventMessage eventMessage = (EventMessage) object;

        checkNotNull(eventMessage);

        final SerializedObject<byte[]> payload = serializer.serializePayload(eventMessage, byte[].class);
        DateTime timestamp = checkNotNull(eventMessage.getTimestamp());
        String className = checkNotNull(eventMessage.getPayloadType().getName());
        MessageBuilderSupport<Message> builder = MessageBuilder.withBody(payload.getData())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType("application/json")
                .setContentEncoding("UTF-8")
                .setMessageId(checkNotNull(eventMessage.getIdentifier()))
                .setType(className)
                .setTimestamp(new Date(eventMessage.getTimestamp().getMillis()))
                .setHeader(SERIALIZER_VERSION_KEY, "1.0")
                .setHeader(EVENT_TIMESTAMP_KEY, timestamp.toString())
                .setHeader(PAYLOAD_REVISION_KEY, checkNotNull(payload.getType().getRevision()))
                .setHeader(PAYLOAD_TYPE_KEY, className)
                .setHeader(EVENT_TYPE_KEY, checkNotNull(EventMessageType.forMessage(eventMessage).getTypeByte()));

        if (eventMessage instanceof DomainEventMessage) {
            final DomainEventMessage domainEventMessage = (DomainEventMessage) eventMessage;
            builder.setHeader(AGGREGATE_ID_KEY, checkNotNull(domainEventMessage.getAggregateIdentifier()))
                   .setHeader(SEQUENCE_NUMBER_KEY, checkNotNull(domainEventMessage.getSequenceNumber()));
        }

        serializeMetadata(eventMessage, builder);

        Message message = builder.build();

        int bodyLength = message.getBody().length;
        Preconditions.checkState(MAX_PAYLOAD_SIZE > bodyLength, MAX_PAYLOAD_SIZE_MESSAGE, MAX_PAYLOAD_SIZE, className, bodyLength);

        return message;
    }

    /**
     * Serialize metadata
     * If null value is encountered for context, then an exception is thrown
     * If null value is encoutnered for other keys, a warn message is logged
     *
     * @param eventMessage event message
     * @param builder builder
     * @throws NullPointerException when encountering null value in property
     */
    private void serializeMetadata(EventMessage eventMessage, MessageBuilderSupport<Message> builder) {
        for (final Map.Entry<String, Object> entry : eventMessage.getMetaData().entrySet()) {
            checkNotNull(entry.getValue(), "encountered null value when serializing metadata %s", entry.getKey());
            if (Context.METANAME.equals(entry.getKey())) {
                final Context context = (Context) entry.getValue();
                for (final Map.Entry<String, String> contextEntry : context.asMap().entrySet()) {
                    checkNotNull(contextEntry.getValue(), "encountered null value in context %s", contextEntry.getKey());
                    builder.setHeader(PREFIX_CONTEXT_KEY + contextEntry.getKey(), contextEntry.getValue());
                }
            } else {
                builder.setHeader(PREFIX_METADATA_KEY + entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {

        try {
            checkNotNull(message);

            MessageProperties messageProperties = message.getMessageProperties();

            final Map<String, Object> headers = messageProperties.getHeaders();
            final DateTime timestamp = new DateTime(checkAndGetHeader(headers, EVENT_TIMESTAMP_KEY));
            final Object payloadType = checkAndGetHeader(headers, PAYLOAD_TYPE_KEY);
            final Object payloadRevision = checkAndGetHeader(headers, PAYLOAD_REVISION_KEY);
            final SimpleSerializedObject<byte[]> serializedPayload = new SimpleSerializedObject<>(
                    message.getBody(),
                    byte[].class,
                    (String) payloadType,
                    (String) payloadRevision
            );

            final Object deserializedObject = serializer.deserialize(serializedPayload);

            if (checkAndGetHeader(headers, EVENT_TYPE_KEY).equals(EventMessageType.EVENT_MESSAGE.getTypeByte())) {
                return new GenericEventMessage<>(
                        messageProperties.getMessageId(),
                        timestamp,
                        deserializedObject,
                        toMetadata(messageProperties)
                );
            }

            return new GenericDomainEventMessage<>(
                    messageProperties.getMessageId(),
                    timestamp,
                    checkAndGetHeader(headers, AGGREGATE_ID_KEY),
                    (Long) checkAndGetHeader(headers, SEQUENCE_NUMBER_KEY),
                    deserializedObject,
                    toMetadata(messageProperties)
            );
        } catch (Exception e) {
            throw new EventBusMessageConversionException(message, e);
        }
    }
}
