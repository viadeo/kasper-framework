// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.context.Context;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.context.HttpContextHeaders.HEADER_TAGS;
import static org.junit.Assert.assertEquals;
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

    @Test
    public void deserialize_withTagsHeader_shouldSetTheContextTags() {
        // Given
        final UUID kasperCorrelationId = UUID.randomUUID();
        final HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader(HEADER_TAGS))
                .thenReturn(",,a-tag,,,,,,,,,another-tag,");

        // When
        final Context context = deserializer.deserialize(req, kasperCorrelationId);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

}
