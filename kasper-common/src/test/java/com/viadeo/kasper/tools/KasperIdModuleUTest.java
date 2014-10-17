// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.impl.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KasperIdModuleUTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new KasperIdModule());
    }

    @Test
    public void serialize_fromDefaultKasperRelationId_isOk() throws IOException {
        // Given
        String sourceId = "83fa9d62-3f38-486c-84c2-4868a1bc8609";
        String targetId = "b54dc4b0-19a8-413b-847c-aab1b6d1c291";
        DefaultKasperRelationId kasperId = new DefaultKasperRelationId(
                new DefaultKasperId(sourceId),
                new DefaultKasperId(targetId)
        );

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("\"" + sourceId + DefaultKasperRelationId.SEPARATOR + targetId + "\"", json);
    }

    @Test
    public void deserialize_toDefaultKasperRelationId_isOk() throws IOException {
        // Given
        String json = "\"83fa9d62-3f38-486c-84c2-4868a1bc8609--b54dc4b0-19a8-413b-847c-aab1b6d1c291\"";
        DefaultKasperRelationId expectedKasperId = new DefaultKasperRelationId(
                new DefaultKasperId("83fa9d62-3f38-486c-84c2-4868a1bc8609"),
                new DefaultKasperId("b54dc4b0-19a8-413b-847c-aab1b6d1c291")
        );

        // When
        DefaultKasperRelationId actualKasperId = objectMapper.readValue(json, DefaultKasperRelationId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @Test
    public void serialize_fromDefaultKasperId_isOk() throws IOException {
        // Given
        String id = "83fa9d62-3f38-486c-84c2-4868a1bc8609";
        DefaultKasperId kasperId = new DefaultKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("\"" + id + "\"", json);
    }

    @Test
    public void deserialize_toDefaultKasperId_isOk() throws IOException {
        // Given
        String json = "\"83fa9d62-3f38-486c-84c2-4868a1bc8609\"";
        DefaultKasperId expectedKasperId = new DefaultKasperId("83fa9d62-3f38-486c-84c2-4868a1bc8609");

        // When
        DefaultKasperId actualKasperId = objectMapper.readValue(json, DefaultKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @Test
    public void serialize_fromIntegerKasperId_isOk() throws IOException {
        // Given
        Integer id = 42;
        IntegerKasperId kasperId = new IntegerKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":" + id + "}", json);
    }

    @Test
    public void deserialize_toIntegerKasperId_isOk() throws IOException {
        // Given
        String json = "{\"id\":42}";
        IntegerKasperId expectedKasperId = new IntegerKasperId(42);

        // When
        IntegerKasperId actualKasperId = objectMapper.readValue(json, IntegerKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @Test
    public void serialize_fromLongKasperId_isOk() throws IOException {
        // Given
        Long id = 42L;
        LongKasperId kasperId = new LongKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":" + id + "}", json);
    }

    @Test
    public void deserialize_toLongKasperId_isOk() throws IOException {
        // Given
        String json = "{\"id\":42}";
        LongKasperId expectedKasperId = new LongKasperId(42L);

        // When
        LongKasperId actualKasperId = objectMapper.readValue(json, LongKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @Test
    public void serialize_fromStringKasperId_isOk() throws IOException {
        // Given
        String id = "pioupiou";
        StringKasperId kasperId = new StringKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":\"" + id + "\"}", json);
    }

    @Test
    public void deserialize_toStringKasperId_isOk() throws IOException {
        // Given
        String json = "\"pioupiou\"";
        StringKasperId expectedKasperId = new StringKasperId("pioupiou");

        // When
        StringKasperId actualKasperId = objectMapper.readValue(json, StringKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }
}
