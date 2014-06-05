// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.amqp;

import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import org.axonframework.domain.*;
import org.axonframework.eventhandling.io.EventMessageType;
import org.axonframework.serializer.MessageSerializer;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.Serializer;
import org.axonframework.serializer.SimpleSerializedObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventMessageConverter implements MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMessageConverter.class);
    private final MessageSerializer serializer;

    final static String HEADER_REQUIRED_MESSAGE = "header %s is required";
    final static String SERIALIZER_VERSION_KEY = "X-SERIALIZER-VERSION";
    final static String AGGREGATE_ID_KEY = "X-AGGREGATE-ID";
    final static String SEQUENCE_NUMBER_KEY = "X-SEQUENCE-NUMBER";

    final static String PAYLOAD_REVISION_KEY = "X-PAYLOAD-REVISION";
    final static String PAYLOAD_TYPE_KEY = "X-PAYLOAD-TYPE";

    final static String EVENT_TYPE_KEY = "X-EVENT-TYPE";
    final static String EVENT_TIMESTAMP_KEY = "X-EVENT-TIMESTAMP";

    final static String PREFIX_METADATA_KEY = "X-META-";
    final static String PREFIX_CONTEXT_KEY = "X-CONTEXT-";


    public EventMessageConverter(final Serializer serializer) {
        this.serializer = new MessageSerializer(checkNotNull(serializer));
    }


    private Object checkAndGetHeader(final Map<String, Object> headers, final String headerName) {
        return checkNotNull(headers.get(headerName), HEADER_REQUIRED_MESSAGE, headerName);
    }

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
        metadata.put(Context.METANAME, new DefaultContext(contextMap));

        return metadata;
    }

    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        EventMessage eventMessage = (EventMessage) object;

        checkNotNull(eventMessage);
        final SerializedObject<byte[]> payload = serializer.serializePayload(eventMessage, byte[].class);
        MessageBuilderSupport<Message> builder = MessageBuilder.withBody(payload.getData())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType("application/json")
                .setContentEncoding("UTF-8")
                .setMessageId(eventMessage.getIdentifier())
                .setType(eventMessage.getPayloadType().getName())
                .setHeader(SERIALIZER_VERSION_KEY, "1.0")
                .setHeader(EVENT_TIMESTAMP_KEY, eventMessage.getTimestamp().toString())
                .setHeader(PAYLOAD_REVISION_KEY, payload.getType().getRevision())
                .setHeader(PAYLOAD_TYPE_KEY, eventMessage.getPayloadType().getName());

        if (eventMessage instanceof DomainEventMessage) {
            final DomainEventMessage domainEventMessage = (DomainEventMessage) eventMessage;
            builder.setHeader(AGGREGATE_ID_KEY, domainEventMessage.getAggregateIdentifier())
                    .setHeader(SEQUENCE_NUMBER_KEY, domainEventMessage.getSequenceNumber());
        }

        if (DomainEventMessage.class.isInstance(eventMessage)) {
            builder.setHeader(EVENT_TYPE_KEY, EventMessageType.DOMAIN_EVENT_MESSAGE.getTypeByte());
        } else {
            builder.setHeader(EVENT_TYPE_KEY, EventMessageType.EVENT_MESSAGE.getTypeByte());
        }

        final MetaData metaData = eventMessage.getMetaData();

        for (final Map.Entry<String, Object> entry : metaData.entrySet()) {
            if (entry.getValue() != null) {
                if (Context.METANAME.equals(entry.getKey())) {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> contextAsMap = (Map<String, Object>) entry.getValue();

                    for (final Map.Entry<String, Object> contextEntry : contextAsMap.entrySet()) {
                        builder.setHeader(PREFIX_CONTEXT_KEY + contextEntry.getKey(), contextEntry.getValue());
                    }
                } else {
                    builder.setHeader(PREFIX_METADATA_KEY + entry.getKey(), entry.getValue());
                }
            } else {
                LOGGER.warn("Unexpected null value for meta named '{}' in message with '{}' id", entry.getKey(), eventMessage.getIdentifier());
            }
        }

        return builder.build();
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
            throw new MessageConversionException("unable to convert message", e);
        }
    }
}
