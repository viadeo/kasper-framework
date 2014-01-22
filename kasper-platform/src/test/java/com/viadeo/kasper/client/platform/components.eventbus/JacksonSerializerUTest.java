// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.SerializedType;
import org.axonframework.serializer.SimpleSerializedObject;
import org.axonframework.serializer.SimpleSerializedType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JacksonSerializerUTest {

    @Mock
    ObjectMapper objectMapper;

    @Test
    public void constructor_withObjectMapper_isOk() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();

        // When
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // Then
        assertNotNull(jacksonSerializer);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withNullAsObjectMapper_throwException() throws Exception {
        // Given
        final ObjectMapper objectMapper = null;

        // When
        new JacksonSerializer(objectMapper);

        // Then throws exception
    }

    @Test
    public void canSerializeTo_withValidObject_returnTrue() throws Exception {
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);
        when(objectMapper.canSerialize(Object.class)).thenReturn(true);

        // When
        boolean b = jacksonSerializer.canSerializeTo(Object.class);

        // Then
        assertTrue(b);
    }

    @Test
    public void canSerializeTo_withInvalidObject_returnFalse() throws Exception {
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);
        when(objectMapper.canSerialize(Object.class)).thenReturn(false);

        // When
        boolean b = jacksonSerializer.canSerializeTo(Object.class);

        // Then
        assertFalse(b);
    }

    @Test(expected = NullPointerException.class)
    public void canSerializeTo_withNullAsObject_throwException() throws Exception {
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // When
        jacksonSerializer.canSerializeTo(null);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void serialize_withNullAsObject_throwException() throws Exception {
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // When
        jacksonSerializer.serialize(null, Object.class);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void serialize_withNullAsExpectedRepresentation_throwException() throws Exception {
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // When
        jacksonSerializer.serialize(new Object(), null);

        // Then throws exception
    }

    @Test
    public void serialize_withValidObject_andItsRepresentation_returnsSerializedObject() throws Exception {
        // Given
        Object object = new Object();
        Class<byte[]> expectedRepresentation = byte[].class;

        byte[] expectedData = "toto".getBytes();
        when(objectMapper.writeValueAsBytes(object)).thenReturn(expectedData);

        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // When
        SerializedObject<byte[]> serializedObject = jacksonSerializer.serialize(object, expectedRepresentation);

        // Then
        assertNotNull(serializedObject);
        assertNotNull(serializedObject.getData());

        assertEquals(expectedData, serializedObject.getData());
        assertEquals(byte[].class, serializedObject.getContentType());
        assertEquals(jacksonSerializer.typeForClass(object.getClass()), serializedObject.getType());
    }

    @Test
    public void deserialize_withValidObject_returnsDeserializedObject() throws Exception {
        // Given
        String object = "{\"foo\":\"bar\"}";
        byte[] expectedData = object.getBytes();
        when(objectMapper.readValue(any(InputStream.class), any(Class.class))).thenReturn(object);

        JacksonSerializer jacksonSerializer = new JacksonSerializer(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        SerializedObject<byte[]> serializedObject = new SimpleSerializedObject<>(
                expectedData, byte[].class, new SimpleSerializedType(Map.class.getName(), "0")
        );

        // When
        Map actualObject = jacksonSerializer.deserialize(serializedObject);

        // Then
        assertNotNull(actualObject);
        assertTrue(actualObject.containsKey("foo"));
        assertEquals(actualObject.get("foo"), "bar");
    }

    @Test
    public void typeForClass_withStringClass_isOk(){
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);
        Class expectedClass = String.class;

        // When
        SerializedType serializedType = jacksonSerializer.typeForClass(expectedClass);

        // Then
        assertNotNull(serializedType);
        assertEquals(String.class.getName(), serializedType.getName());
        assertEquals("0", serializedType.getRevision());
    }

    @Test(expected = NullPointerException.class)
    public void typeForClass_withNullAsClass_throwException(){
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);

        // When
        jacksonSerializer.typeForClass(null);

        // Then throw exception
    }

    @Test
    public void classForType_withValidSerializedType_isOk(){
        // Given
        JacksonSerializer jacksonSerializer = new JacksonSerializer(objectMapper);
        SimpleSerializedType serializedType = new SimpleSerializedType(String.class.getName(), "0");

        // When
        Class actualClass = jacksonSerializer.classForType(serializedType);

        // Then
        assertNotNull(actualClass);
        assertEquals(serializedType.getName(), actualClass.getName());
    }

}
