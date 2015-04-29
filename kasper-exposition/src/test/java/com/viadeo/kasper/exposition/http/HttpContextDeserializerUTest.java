// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpContextDeserializerUTest {

    HttpContextDeserializer deserializer;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        deserializer = new HttpContextDeserializer();
    }

    // ------------------------------------------------------------------------

    private HttpServletRequest mockHttpServletRequest(final Map<String, Serializable> properties){
        HttpServletRequest req = mock(HttpServletRequest.class);

        final Iterator<String> iterator = properties.keySet().iterator();
        when(req.getHeaderNames()).thenReturn(new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        });
        for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
            when(req.getHeader(entry.getKey())).thenReturn(String.valueOf(entry.getValue()));
        }
        return req;
    }

    @Test
    public void deserialize_withNotIdentifiedHeader_isOk() {
        // Given
        final UUID kasperCorrelationId = UUID.randomUUID();
        final HttpServletRequest req = mockHttpServletRequest(
                ImmutableMap.<String,Serializable>builder()
                        .put("aKey", "aValue")
                        .build()
        );

        // When
        final Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertNotNull(context);
        assertTrue(context.getProperty("aKey").isPresent());
        assertEquals("aValue", context.getProperty("aKey").get());
    }

    @Test
    public void deserialize_withIdentifiedHeader_isOk() {
        // Given
        ImmutableMap.Builder<String, Serializable> builder = ImmutableMap.builder();
        for (HttpContextHeaders header : HttpContextHeaders.values()) {
            builder.put(header.toHeaderName(), UUID.randomUUID().toString());
        }

        final Map<String, Serializable> properties = builder.build();
        final UUID kasperCorrelationId = UUID.randomUUID();
        final HttpServletRequest req = mockHttpServletRequest(properties);

        // When
        final Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertNotNull(context);

        for (HttpContextHeaders header : HttpContextHeaders.values()) {
            assertTrue(context.getProperty(header.toPropertyKey()).isPresent());
            assertEquals(properties.get(header.toHeaderName()), context.getProperty(header.toPropertyKey()).get());
        }
    }

    @Test
    public void deserialize_withTagsHeader_shouldSetTheContextTags() {
        // Given
        final UUID kasperCorrelationId = UUID.randomUUID();
        final HttpServletRequest req = mockHttpServletRequest(
                ImmutableMap.<String, Serializable>builder()
                        .put(HEADER_TAGS.toHeaderName(), ",,a-tag,,,,,,,,,another-tag,")
                        .build()
        );

        // When
        final Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertNotNull(context);
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

}
