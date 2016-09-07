// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
