package com.viadeo.kasper.client;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.google.common.collect.Sets;
import com.sun.jersey.api.client.RequestBuilder;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;

public class HttpContextSerializerUTest {

    HttpContextSerializer serializer;

    @Before
    public void setUp() {
        serializer = new HttpContextSerializer();
    }

    @Test
    public void serialize_withNoTags_shouldNotSetHeader() {
        // Given
        RequestBuilder builder = mock(RequestBuilder.class);

        Context context = new DefaultContext();
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
        RequestBuilder builder = mock(RequestBuilder.class);

        Context context = new DefaultContext();
        context.setTags(newHashSet("a-tag", "another-tag"));

        // When
        serializer.serialize(context, builder);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        InOrder inOrder = inOrder(builder);
        inOrder.verify(builder)
                .header(eq(HEADER_TAGS), captor.capture());
        inOrder.verify(builder, never())
                .header(eq(HEADER_TAGS), anyString()); // once and only once

        String value = captor.getValue();
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(value)); // order is not important
    }

}
