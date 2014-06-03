// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rabbitmq.client.AMQP;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import org.axonframework.domain.*;
import org.axonframework.eventhandling.amqp.AMQPMessage;
import org.axonframework.eventhandling.amqp.AMQPMessageConverter;
import org.axonframework.eventhandling.amqp.RoutingKeyResolver;
import org.axonframework.eventhandling.io.EventMessageType;
import org.axonframework.serializer.MessageSerializer;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.Serializer;
import org.axonframework.serializer.SimpleSerializedObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultMessageConverter implements AMQPMessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageConverter.class);
    private final MessageSerializer serializer;
    private final RoutingKeyResolver routingKeyResolver;

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


    public DefaultMessageConverter(final Serializer serializer, final RoutingKeyResolver routingKeyResolver) {
        this.serializer = new MessageSerializer(checkNotNull(serializer));
        this.routingKeyResolver = checkNotNull(routingKeyResolver);
    }

    @Override
    public AMQPMessage createAMQPMessage(final EventMessage eventMessage) {
        checkNotNull(eventMessage);

        final SerializedObject<byte[]> payload = serializer.serializePayload(eventMessage, byte[].class);

        final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentType("application/json")
                .contentEncoding("UTF-8")
                .messageId(eventMessage.getIdentifier())
                .type(eventMessage.getPayloadType().getName());

        final ImmutableMap.Builder<String, Object> headers = ImmutableMap.<String, Object>builder()
                .put(SERIALIZER_VERSION_KEY, "1.0")
                .put(EVENT_TIMESTAMP_KEY, eventMessage.getTimestamp().toString());

        headers.put(PAYLOAD_REVISION_KEY, payload.getType().getRevision());
        headers.put(PAYLOAD_TYPE_KEY, eventMessage.getPayloadType().getName());

        if (eventMessage instanceof DomainEventMessage) {
            final DomainEventMessage domainEventMessage = (DomainEventMessage) eventMessage;

            headers.put(AGGREGATE_ID_KEY, domainEventMessage.getAggregateIdentifier());
            headers.put(SEQUENCE_NUMBER_KEY, domainEventMessage.getSequenceNumber());
        }

        if (DomainEventMessage.class.isInstance(eventMessage)) {
            headers.put(EVENT_TYPE_KEY, EventMessageType.DOMAIN_EVENT_MESSAGE.getTypeByte());
        } else {
            headers.put(EVENT_TYPE_KEY, EventMessageType.EVENT_MESSAGE.getTypeByte());
        }

        final MetaData metaData = eventMessage.getMetaData();

        for (final Map.Entry<String, Object> entry : metaData.entrySet()) {
            if (entry.getValue() != null) {
                if (Context.METANAME.equals(entry.getKey())) {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> contextAsMap = (Map<String, Object>) entry.getValue();

                    for (final Map.Entry<String, Object> contextEntry : contextAsMap.entrySet()) {
                        headers.put(PREFIX_CONTEXT_KEY + contextEntry.getKey(), contextEntry.getValue());
                    }
                } else {
                    headers.put(PREFIX_METADATA_KEY + entry.getKey(), entry.getValue());
                }
            } else {
                LOGGER.warn("Unexpected null value for meta named '{}' in message with '{}' id", entry.getKey(), eventMessage.getIdentifier());
            }
        }

        builder.headers(headers.build());

        return new AMQPMessage(
                payload.getData(),
                routingKeyResolver.resolveRoutingKey(eventMessage),
                builder.build(),
                false,
                false
        );
    }

    @Override
    public EventMessage readAMQPMessage(final byte[] payload, final MessageProperties properties) {
        checkNotNull(payload);
        checkNotNull(properties);


        final Map<String, Object> headers = properties.getHeaders();

        final DateTime timestamp = new DateTime(checkAndGetHeader(headers, EVENT_TIMESTAMP_KEY));

        final Object payloadType = checkAndGetHeader(headers, PAYLOAD_TYPE_KEY);
        final Object payloadRevision = checkAndGetHeader(headers, PAYLOAD_REVISION_KEY);

        final SimpleSerializedObject<byte[]> serializedPayload = new SimpleSerializedObject<>(
                payload,
                byte[].class,
                (String) payloadType,
                (String) payloadRevision
        );

        final Object deserializedObject = serializer.deserialize(serializedPayload);

        if (checkAndGetHeader(headers, EVENT_TYPE_KEY).equals(EventMessageType.EVENT_MESSAGE.getTypeByte())) {
            return new GenericEventMessage<>(
                    properties.getMessageId(),
                    timestamp,
                    deserializedObject,
                    toMetadata(properties)
            );
        }

        return new GenericDomainEventMessage<>(
                properties.getMessageId(),
                timestamp,
                checkAndGetHeader(headers, AGGREGATE_ID_KEY),
                (Long) checkAndGetHeader(headers, SEQUENCE_NUMBER_KEY),
                deserializedObject,
                toMetadata(properties)
        );
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
        metadata.put(Context.METANAME, new DefaultContext(contextMap).asMetaDataMap());

        return metadata;
    }

}
