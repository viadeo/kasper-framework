// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.collect.Sets;
import com.sun.jersey.api.client.RequestBuilder;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpContextSerializerUTest {

    HttpContextSerializer serializer;

    @Before
    public void setUp() {
        serializer = new HttpContextSerializer();
    }

    @Test
    public void serialize_withNotIdentifiedHeader_isOk() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);
        final Context context = Contexts.builder().with("aKey", "aValue").build();

        // When
        serializer.serialize(context, builder);

        // Then
        verify(builder).header(eq("aKey"), eq("aValue"));
    }

    @Test
    public void serialize_withIdentifiedHeader_isOk() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);
        final Context.Builder contextBuilder = Contexts.builder();
        for (HttpContextHeaders header : HttpContextHeaders.values()) {
            contextBuilder.with(header.toPropertyKey(), UUID.randomUUID().toString());
        }
        final Context context = contextBuilder.build();

        // When
        serializer.serialize(context, builder);

        // Then
        for (HttpContextHeaders header : HttpContextHeaders.values()) {
            assertTrue(context.getProperty(header.toPropertyKey()).isPresent());
            verify(builder).header(eq(header.toHeaderName()), eq(context.getProperty(header.toPropertyKey()).get()));
        }
    }

    @Test
    public void serialize_withNoTags_shouldNotSetHeader() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);
        final Context context = Contexts.builder().withTags(Sets.<String>newHashSet()).build();

        // When
        serializer.serialize(context, builder);

        // Then
        verify(builder, never())
                .header(eq(HttpContextHeaders.HEADER_TAGS.toHeaderName()), anyString());
    }

    @Test
    public void serialize_withTags_shouldSetHeader() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);

        final Context context = Contexts.builder().addTags(Sets.newHashSet("a-tag", "another-tag")).build();

        // When
        serializer.serialize(context, builder);

        // Then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        final InOrder inOrder = inOrder(builder);
        inOrder.verify(builder)
                .header(eq(HttpContextHeaders.HEADER_TAGS.toHeaderName()), captor.capture());
        inOrder.verify(builder, never())
                .header(eq(HttpContextHeaders.HEADER_TAGS.toHeaderName()), anyString()); // once and only once

        final String value = captor.getValue();
        assertTrue(Sets.newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(value)); // order is not important
    }

}
