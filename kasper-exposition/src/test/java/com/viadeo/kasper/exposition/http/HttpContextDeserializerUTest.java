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
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import com.viadeo.kasper.core.context.DefaultContextHelper;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.common.exposition.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpContextDeserializerUTest {

    HttpContextDeserializer deserializer;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        deserializer = new HttpContextWithVersionDeserializer(new DefaultContextHelper(new SimpleIDBuilder()));
    }

    // ------------------------------------------------------------------------

    private HttpServletRequest mockHttpServletRequest(final Map<String, Serializable> properties) {
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
                ImmutableMap.<String, Serializable>builder()
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
    public void deserialize_without_kasperId_in_header_should_generate_id() {
        // Given
        ImmutableMap.Builder<String, Serializable> builder = ImmutableMap.builder();
        for (HttpContextHeaders header : HttpContextHeaders.values()) {
            if (header != HttpContextHeaders.HEADER_KASPER_ID) {
                builder.put(header.toHeaderName(), UUID.randomUUID().toString());
            }
        }


        final Map<String, Serializable> properties = builder.build();
        final UUID kasperCorrelationId = UUID.randomUUID();
        final HttpServletRequest req = mockHttpServletRequest(properties);

        // When
        final Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertNotNull(context);

        assertEquals(kasperCorrelationId.toString(), context.getKasperCorrelationId().get());
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
