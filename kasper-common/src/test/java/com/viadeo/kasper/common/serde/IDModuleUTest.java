// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class IDModuleUTest {
    
    private static final String VENDOR = "vendor";
    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(final String identifier) {
            return (E) new Integer(identifier);
        }
    };

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        IDBuilder builder = new SimpleIDBuilder(DB_ID);
        IDModule module = new IDModule(builder);

        mapper = new ObjectMapper();
        mapper.registerModule(module);
    }

    @Test
    public void serialize_isOk() throws JsonProcessingException {
        // Given
        ID id = new ID(VENDOR, "member", DB_ID, 42);

        // When
        String serializedId = mapper.writeValueAsString(id);

        // Then
        assertNotNull(serializedId);
        Assert.assertEquals("\"" + id.toString() + "\"", serializedId);
    }

    @Test
    public void deserialize_isOk() throws IOException {
        // Given
        String serializedId = "\"urn:viadeo:member:db-id:42\"";

        // When
        ID deserializedId = mapper.readValue(serializedId, ID.class);

        // Then
        assertNotNull(deserializedId);
        Assert.assertEquals("member", deserializedId.getType());
        Assert.assertEquals(DB_ID, deserializedId.getFormat());
        Assert.assertEquals("42", deserializedId.getIdentifier());
    }

    @Test
    public void serialize_then_deserialize_isOk() throws IOException {
        // Given
        ID givenId = new ID(VENDOR, "member", DB_ID, 42);

        // When
        ID actualId = mapper.readValue(mapper.writeValueAsString(givenId), ID.class);

        // Then
        assertNotNull(actualId);
        Assert.assertEquals(givenId.getVendor(), actualId.getVendor());
        Assert.assertEquals(givenId.getType(), actualId.getType());
        Assert.assertEquals(givenId.getFormat(), actualId.getFormat());
        Assert.assertEquals(givenId.getIdentifier(), actualId.getIdentifier());
    }
}
