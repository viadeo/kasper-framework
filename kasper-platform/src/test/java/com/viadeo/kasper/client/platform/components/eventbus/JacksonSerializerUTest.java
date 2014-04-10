// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.MetaData;
import org.axonframework.serializer.SerializedObject;
import org.axonframework.serializer.SerializedType;
import org.axonframework.serializer.SimpleSerializedObject;
import org.axonframework.serializer.SimpleSerializedType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JacksonSerializerUTest {

    private JacksonSerializer jacksonSerializer;


    @Before
    public void setUp() throws Exception {
        jacksonSerializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());
    }

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
        ObjectMapper objectMapper = mock(ObjectMapper.class);
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
        ObjectMapper objectMapper = mock(ObjectMapper.class);
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

        // When
        jacksonSerializer.canSerializeTo(null);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void serialize_withNullAsObject_throwException() throws Exception {
        // Given

        // When
        jacksonSerializer.serialize(null, Object.class);

        // Then throws exception
    }

    @Test(expected = NullPointerException.class)
    public void serialize_withNullAsExpectedRepresentation_throwException() throws Exception {
        // Given

        // When
        jacksonSerializer.serialize(new Object(), null);

        // Then throws exception
    }

    @Test
    public void serialize_withValidObject_andItsRepresentation_returnsSerializedObject() throws Exception {
        // Given
        ImmutableMap<String, String> input = ImmutableMap.of("foo", "bar");

        // When
        SerializedObject<byte[]> serializedObject = jacksonSerializer.serialize(input, byte[].class);

        // Then
        assertNotNull(serializedObject);
        assertEquals("{\"foo\":\"bar\"}", new String(serializedObject.getData()));
        assertEquals(byte[].class, serializedObject.getContentType());
        assertEquals(jacksonSerializer.typeForClass(input.getClass()), serializedObject.getType());
    }

    @Test
    public void deserialize_withValidObject_returnsDeserializedObject() throws Exception {
        // Given
        String object = "{\"foo\":\"bar\"}";

        SerializedObject<byte[]> serializedObject = new SimpleSerializedObject<>(
                object.getBytes(), byte[].class, new SimpleSerializedType(Map.class.getName(), "0")
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

        // When
        jacksonSerializer.typeForClass(null);

        // Then throw exception
    }

    @Test
    public void classForType_withValidSerializedType_isOk(){
        // Given
        SimpleSerializedType serializedType = new SimpleSerializedType(String.class.getName(), "0");

        // When
        Class actualClass = jacksonSerializer.classForType(serializedType);

        // Then
        assertNotNull(actualClass);
        assertEquals(serializedType.getName(), actualClass.getName());
    }

    @Test
    public void deserialize_withMetaData_isOk(){
        // Given
        final JacksonSerializer jacksonSerializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());

        final MetaData metaData = new MetaData(ImmutableMap.<String,String>builder().put("foo", "bar").build());

        final SerializedObject<byte[]> serializedObject = jacksonSerializer.serialize(metaData, byte[].class);

        // When
        final MetaData actualMetaData = jacksonSerializer.deserialize(serializedObject);

        // Then
        assertNotNull(actualMetaData);
        assertEquals(metaData, actualMetaData);
    }

    @Test
    public void deserialize_withMetaData_containingContext_isOk(){
        // Given
        final JacksonSerializer jacksonSerializer = new JacksonSerializer(ObjectMapperProvider.INSTANCE.mapper());

        final Context context = new DefaultContext();
        context.setFunnelCorrelationId("funnelCorrelationId");
        context.setFunnelName("funnelName");
        context.setFunnelVersion("0.1");
        context.setRequestCorrelationId("requestCorrelationId");
        context.setSecurityToken("securityToken");
        context.setUserCountry("fr");
        context.setUserId("42");
        context.setUserLang("fr");
        context.setSessionCorrelationId("sessionCorrelationId");
        context.setApplicationId("TEST");
        context.setIpAddress("127.0.0.1");

        final MetaData metaData = new MetaData(
                ImmutableMap.<String,Object>builder()
                        .put("foo", "bar")
                        .put(Context.METANAME, context)
                        .build()
        );

        final SerializedObject<byte[]> serializedObject = jacksonSerializer.serialize(metaData, byte[].class);

        // When
        final MetaData actualMetaData = jacksonSerializer.deserialize(serializedObject);

        // Then
        assertNotNull(actualMetaData);

        final Context actualContext = (Context) actualMetaData.get(Context.METANAME);
        assertNotNull(actualContext);
        assertEquals(context, actualContext);
    }

}
