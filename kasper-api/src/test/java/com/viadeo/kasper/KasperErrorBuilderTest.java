// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.KasperError.Builder;
import static org.junit.Assert.assertEquals;

public class KasperErrorBuilderTest {

    @Test
    public void testEmpty() {
        // Given
        final Builder builder = Builder.empty();

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals(CoreErrorCode.UNKNOWN_ERROR.string(), error.getCode());
        assertEquals(0, error.getMessages().size());
    }

    @Test
    public void testFromCoreCode() {
        // Given
        final Builder builder = Builder.from(CoreErrorCode.UNKNOWN_ERROR);

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals(CoreErrorCode.UNKNOWN_ERROR.string(), error.getCode());
        assertEquals(0, error.getMessages().size());
    }

    @Test
    public void testFromCoreCodeAndMessagesCollection() {
        // Given
        final Collection<String> messages = new ArrayList<String>() {{
            add("foo");
        }};
        final Builder builder = Builder.from(CoreErrorCode.UNKNOWN_ERROR, messages);

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals(CoreErrorCode.UNKNOWN_ERROR.string(), error.getCode());
        assertEquals(1, error.getMessages().size());
        assertEquals("foo", error.getMessages().toArray()[0]);
    }

    @Test
    public void testFromCoreCodeAndMessages() {
        // Given
        final Builder builder = Builder.from(CoreErrorCode.UNKNOWN_ERROR, "foo", "bar");

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals(CoreErrorCode.UNKNOWN_ERROR.string(), error.getCode());
        assertEquals(2, error.getMessages().size());
        assertEquals("foo", error.getMessages().toArray()[0]);
        assertEquals("bar", error.getMessages().toArray()[1]);
    }

    @Test
    public void testFromKasperError() {
        // Given
        final Builder builder = Builder.from(new KasperError(
                "code",
                "foo",
                "bar"
        ));

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals("code", error.getCode());
        assertEquals(2, error.getMessages().size());
        assertEquals("foo", error.getMessages().toArray()[0]);
        assertEquals("bar", error.getMessages().toArray()[1]);
    }

    @Test
    public void testFromCodeAndMessagesCollection() {
        // Given
        final Collection<String> messages = new ArrayList<String>() {{
            add("foo");
        }};
        final Builder builder = Builder.from("code", messages);

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals("code", error.getCode());
        assertEquals(1, error.getMessages().size());
        assertEquals("foo", error.getMessages().toArray()[0]);
    }

    @Test
    public void testFromCodeAndMessages() {
        // Given
        final Builder builder = Builder.from("foo", "foo", "bar");

        // When
        final KasperError error = builder.build();

        // Then
        assertEquals("foo", error.getCode());
        assertEquals(2, error.getMessages().size());
        assertEquals("foo", error.getMessages().toArray()[0]);
        assertEquals("bar", error.getMessages().toArray()[1]);
    }

    /*

    public static Builder from(final CoreErrorCode code, final Collection<String> messages) {
        return from(code.string(), (String[]) messages.toArray());
    }

    public static Builder from(final CoreErrorCode code, final String...messages) {
        return from(code.string(), messages);
    }
    */

}
