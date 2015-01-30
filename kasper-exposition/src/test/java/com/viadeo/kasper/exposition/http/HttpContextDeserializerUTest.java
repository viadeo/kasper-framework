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
    @SuppressWarnings("all")
    public void deserialize_withMissingTagsHeader_shouldReturnTagEmptyComtext() {
        // Given
        UUID kasperCorrelationId = UUID.randomUUID();
        HttpServletRequest req = mock(HttpServletRequest.class);

        // When
        Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertEquals(newHashSet(), context.getTags());
    }

    @Test
    public void deserialize_withTagsHeader_shouldReturnTheTag() {
        // Given
        UUID kasperCorrelationId = UUID.randomUUID();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(HEADER_TAGS))
                .thenReturn("a-tag");

        // When
        Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertEquals(newHashSet("a-tag"), context.getTags());
    }

    @Test
    public void deserialize_withTagsHeader_shouldSplitTheTags() {
        // Given
        UUID kasperCorrelationId = UUID.randomUUID();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(HEADER_TAGS))
                .thenReturn("a-tag,another-tag");

        // When
        Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

    @Test
    public void deserialize_withTagsHeader_shouldOmitEmptyTags() {
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
