// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.collect.Sets;
import com.sun.jersey.api.client.RequestBuilder;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class HttpContextSerializerUTest {

    HttpContextSerializer serializer;

    @Before
    public void setUp() {
        serializer = new HttpContextSerializer();
    }

    @Test
    public void serialize_withNoTags_shouldNotSetHeader() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);

        final Context context = new DefaultContext();
        context.setTags(Sets.<String>newHashSet());

        // When
        serializer.serialize(context, builder);

        // Then
        verify(builder, never())
                .header(eq(HEADER_TAGS), anyString());
    }

    @Test
    public void serialize_withTags_shouldSetHeader() {
        // Given
        final RequestBuilder builder = mock(RequestBuilder.class);

        final Context context = new DefaultContext();
        context.setTags(newHashSet("a-tag", "another-tag"));

        // When
        serializer.serialize(context, builder);

        // Then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        final InOrder inOrder = inOrder(builder);
        inOrder.verify(builder)
                .header(eq(HEADER_TAGS), captor.capture());
        inOrder.verify(builder, never())
                .header(eq(HEADER_TAGS), anyString()); // once and only once

        final String value = captor.getValue();
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(value)); // order is not important
    }

}
