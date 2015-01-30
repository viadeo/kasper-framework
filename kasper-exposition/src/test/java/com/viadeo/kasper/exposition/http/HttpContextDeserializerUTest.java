package com.viadeo.kasper.exposition.http;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.viadeo.kasper.context.Context;

public class HttpContextDeserializerUTest {

    HttpContextDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new HttpContextDeserializer();
    }

    @Test
    public void deserialize_withTagsHeader_shouldSetTheContextTags() {
        // Given
        UUID kasperCorrelationId = UUID.randomUUID();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(HEADER_TAGS))
                .thenReturn(",,a-tag,,,,,,,,,another-tag,");

        // When
        Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

}
