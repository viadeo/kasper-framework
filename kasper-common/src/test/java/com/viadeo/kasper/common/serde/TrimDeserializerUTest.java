// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrimDeserializerUTest {

    private TrimDeserializer trimDeserializer;

    @Mock
    JsonParser jsonParser;

    @Mock
    DeserializationContext deserializationContext;

    @Before
    public void setUp() throws Exception {
        StringDeserializer stdDeserializer = new StringDeserializer();
        trimDeserializer = new TrimDeserializer(stdDeserializer);
    }

    @Test
    public void testDeserialize() throws Exception {

        // Given
        when(jsonParser.getValueAsString()).thenReturn(" gnarf ");

        // When
        String output = trimDeserializer.deserialize(jsonParser, deserializationContext);

        // Then
        assertEquals(output, "gnarf");
    }

    @Test
    public void testDeserializeWhenJsonParserReturnsNull() throws Exception {

        // Given
        when(jsonParser.getValueAsString()).thenReturn(null);
        when(jsonParser.getCurrentToken()).thenReturn(JsonToken.VALUE_EMBEDDED_OBJECT);

        // When
        String output = trimDeserializer.deserialize(jsonParser, deserializationContext);

        // Then
        assertNull(output);
    }
}