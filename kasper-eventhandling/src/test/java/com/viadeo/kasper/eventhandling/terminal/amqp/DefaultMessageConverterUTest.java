// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericDomainEventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.amqp.AMQPMessage;
import org.axonframework.eventhandling.amqp.RoutingKeyResolver;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.Serializer;
import org.axonframework.serializer.SimpleSerializedObject;
import org.axonframework.serializer.SimpleSerializedType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;

import java.util.HashMap;
import java.util.Map;

import static com.viadeo.kasper.eventhandling.terminal.amqp.DefaultMessageConverter.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageConverterUTest {

    @Mock
    private Serializer serializer;

    @Mock
    private RoutingKeyResolver routingKeyResolver;

    private DefaultMessageConverter converter;
    private DateTime timestamp;
    private Map<String, Object> properties;

    @Before
    public void setUp() throws Exception {
        converter = new DefaultMessageConverter(serializer, routingKeyResolver);
        timestamp = new DateTime("2012-10-12");
        properties = ImmutableMap.<String, Object>builder()
                .put("foo", "bar")
                .build();
    }

    @Test
    public void createAMQPMessage_withDomainEventMessage_isOk() throws Exception {
        // Given
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject<>("payload".getBytes(), byte[].class, new SimpleSerializedType("bytes", "payload-revision"));
        when(serializer.serialize(anyObject(), any(Class.class))).thenReturn(serializedObject);

        final GenericDomainEventMessage<String> message = new GenericDomainEventMessage<>("event-id", timestamp, "event-aggregate-id", 1L, "payload", properties);

        when(routingKeyResolver.resolveRoutingKey(refEq(message))).thenReturn("myWonderfulRoutingKey");

        // When
        final AMQPMessage amqpMessage = converter.createAMQPMessage(message);

        // Then
        assertNotNull(amqpMessage);
        assertEquals("payload", new String(amqpMessage.getBody()));
        assertEquals("myWonderfulRoutingKey", amqpMessage.getRoutingKey());

        final AMQP.BasicProperties actualProperties = amqpMessage.getProperties();
        assertEquals(Integer.valueOf(2), actualProperties.getDeliveryMode());
        assertEquals("event-id", actualProperties.getMessageId());
        assertEquals("application/json", actualProperties.getContentType());
        assertEquals("UTF-8", actualProperties.getContentEncoding());
        assertEquals("java.lang.String", actualProperties.getType());

        final Map<String, Object> headers = actualProperties.getHeaders();
        assertTrue(headers.containsKey(PAYLOAD_REVISION_KEY));
        assertTrue(headers.containsKey(PAYLOAD_TYPE_KEY));
        assertEquals("1.0", headers.get(SERIALIZER_VERSION_KEY));
        assertEquals("2012-10-12T00:00:00.000+02:00", headers.get(EVENT_TIMESTAMP_KEY));
        assertEquals("event-aggregate-id", headers.get(AGGREGATE_ID_KEY));
        assertEquals(1L, headers.get(SEQUENCE_NUMBER_KEY));
        assertEquals((byte) 3, headers.get(EVENT_TYPE_KEY));
        assertEquals("payload-revision", headers.get(PAYLOAD_REVISION_KEY));
        assertEquals("bar", headers.get(PREFIX_METADATA_KEY + "foo"));
    }

    @Test
    public void createAMQPMessage_withEventMessage_isOk() throws Exception {
        // Given
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject<>("payload".getBytes(), byte[].class, new SimpleSerializedType("bytes", "payload-revision"));
        when(serializer.serialize(anyObject(), any(Class.class))).thenReturn(serializedObject);

        GenericEventMessage<String> message = new GenericEventMessage<>("event-id", timestamp, "payload", properties);

        // When
        final AMQPMessage amqpMessage = converter.createAMQPMessage(message);

        // Then
        assertNotNull(amqpMessage);

        final Map<String, Object> headers = amqpMessage.getProperties().getHeaders();
        assertEquals((byte) 1, headers.get(EVENT_TYPE_KEY));
        assertFalse(headers.containsKey(AGGREGATE_ID_KEY));
        assertFalse(headers.containsKey(SEQUENCE_NUMBER_KEY));
    }

    @Test(expected = NullPointerException.class)
    public void createAMQPMessage_withNullAsEventMessage_throwException() throws Exception {
        // Given nothing
        // When
        converter.createAMQPMessage(null);
        // Then throw exception
    }

    @Test
    public void createAMQPMessage_withNullAsValueOfContextProperties_isOk() {
        // Given
        properties = new HashMap<>(properties);
        properties.put("lolilou", null);

        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject<>("payload".getBytes(), byte[].class, new SimpleSerializedType("bytes", "payload-revision"));
        when(serializer.serialize(anyObject(), any(Class.class))).thenReturn(serializedObject);

        final GenericEventMessage<String> message = new GenericEventMessage<>("event-id", timestamp, "payload", properties);

        // When
        converter.createAMQPMessage(message);

        // Then no exception is thrown
    }

    @Test
    public void readAMQPMessage_fromDomainEventMessage_isOk() throws Exception {
        // Given
        final String payload = "foobar";

        when(serializer.deserialize(any(SerializedObject.class))).thenReturn(payload);


        MessageProperties properties = new MessageProperties();

        properties.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
        properties.setContentEncoding("charset=utf-8");
        properties.setContentType("application/json");
        properties.setType("java.lang.String");
        properties.setHeader(AGGREGATE_ID_KEY, "aggregate-id");
        properties.setHeader(PAYLOAD_TYPE_KEY, payload.getClass().getName());
        properties.setHeader(PAYLOAD_REVISION_KEY, "0");
        properties.setHeader(SEQUENCE_NUMBER_KEY, 1L);
        properties.setHeader(EVENT_TYPE_KEY, "");
        properties.setHeader(EVENT_TIMESTAMP_KEY, "2012-10-12T00:00:00.000+02:00");
        properties.setHeader(SERIALIZER_VERSION_KEY, "1.0");

        // When
        final EventMessage eventMessage = converter.readAMQPMessage(payload.getBytes(), properties);

        // Then
        assertNotNull(eventMessage);
        assertTrue(eventMessage instanceof GenericDomainEventMessage);
        assertEquals(payload, eventMessage.getPayload());
        assertEquals(payload.getClass(), eventMessage.getPayloadType());

        final GenericDomainEventMessage domainEventMessage = (GenericDomainEventMessage) eventMessage;
        assertEquals(1L, domainEventMessage.getSequenceNumber());
        assertEquals("aggregate-id", domainEventMessage.getAggregateIdentifier());

    }

    @Test
    public void readAMQPMessage_fromEventMessage_isOk() throws Exception {
        // Given
        final String payload = "toto";

        when(serializer.deserialize(any(SerializedObject.class))).thenReturn(payload);

        MessageProperties properties = new MessageProperties();

        properties.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
        properties.setContentEncoding("charset=utf-8");
        properties.setContentType("application/json");
        properties.setType("java.lang.String");
        properties.setHeader(AGGREGATE_ID_KEY, "aggregate-id");
        properties.setHeader(PAYLOAD_TYPE_KEY, payload.getClass().getName());
        properties.setHeader(PAYLOAD_REVISION_KEY, "0");
        properties.setHeader(SEQUENCE_NUMBER_KEY, 1L);
        properties.setHeader(EVENT_TYPE_KEY, "");
        properties.setHeader(EVENT_TIMESTAMP_KEY, "2012-10-12T00:00:00.000+02:00");
        properties.setHeader(SERIALIZER_VERSION_KEY, "1.0");

        // When
        final EventMessage eventMessage = converter.readAMQPMessage(payload.getBytes(), properties);

        // Then
        assertNotNull(eventMessage);
        assertTrue(eventMessage instanceof GenericEventMessage);
        assertEquals(payload, eventMessage.getPayload());
        assertEquals(payload.getClass(), eventMessage.getPayloadType());
    }

    @Test(expected = NullPointerException.class)
    public void readAMQPMessage_withNullAsByteArray_throwException() throws Exception {
        // Given nothing
        // When
        converter.readAMQPMessage(null, new MessageProperties());
        // Then throw exception
    }

    @Test(expected = NullPointerException.class)
    public void readAMQPMessage_withNullAsProperties_throwException() throws Exception {
        // Given nothing
        // When
        converter.readAMQPMessage("foo".getBytes(), null);
        // Then throw exception
    }

    @Test(expected = NullPointerException.class)
    public void toMetadata_withNullAsProperties_throwException() {
        // Given nothing
        // When
        converter.toMetadata(null);
        // Then throw exception
    }

    @Test()
    public void toMetadata_withMap_withHeaders_throwException() {
        // Given

        MessageProperties properties = new MessageProperties();

        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setContentEncoding("charset=utf-8");
        properties.setContentType("application/json");
        properties.setType("java.lang.String");
        properties.setHeader(PREFIX_CONTEXT_KEY + Context.ULANG_SHORTNAME, "fr");


        // When
        final Map<String, ?> metadata = converter.toMetadata(properties);

        // Then
        assertNotNull(metadata);
        assertEquals(2, metadata.get("delivery-mode"));
        assertEquals("charset=utf-8", metadata.get("content-encoding"));
        assertEquals("application/json", metadata.get("content-type"));
        assertEquals("java.lang.String", metadata.get("type"));

        final Object object = metadata.get(Context.METANAME);
        assertNotNull(object);
        assertTrue(object instanceof Map);

        @SuppressWarnings("unchecked")
        final Context context = new DefaultContext((Map) object);
        assertEquals("fr", context.getUserLang());
    }

    @Test()
    public void toMetadata_withMap_withoutHeaders_throwException() {
        // Given


        MessageProperties properties = new MessageProperties();

        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setContentEncoding("charset=utf-8");
        properties.setContentType("application/json");
        properties.setType("java.lang.String");

        // When
        final Map<String, ?> metadata = converter.toMetadata(properties);

        // Then
        assertNotNull(metadata);
        assertEquals(2, metadata.get("delivery-mode"));
        assertEquals("charset=utf-8", metadata.get("content-encoding"));
        assertEquals("application/json", metadata.get("content-type"));
        assertEquals("java.lang.String", metadata.get("type"));
    }
}