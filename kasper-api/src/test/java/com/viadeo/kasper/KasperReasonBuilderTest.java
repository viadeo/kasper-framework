// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static com.viadeo.kasper.KasperReason.Builder;
import static org.junit.Assert.assertEquals;

public class KasperReasonBuilderTest {

    @Test
    public void testEmpty() {
        // Given
        final Builder builder = Builder.empty();

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), reason.getReasonCode().intValue());
        assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), reason.getLabel());
        assertEquals(0, reason.getMessages().size());
    }

    @Test
    public void testFromCoreCode() {
        // Given
        final Builder builder = Builder.from(CoreReasonCode.UNKNOWN_REASON);

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), reason.getLabel());
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), reason.getReasonCode().intValue());
        assertEquals(0, reason.getMessages().size());
    }

    @Test
    public void testFromCoreCodeAndMessagesCollection() {
        // Given
        final Collection<String> messages = new ArrayList<String>() {{
            add("foo");
        }};
        final Builder builder = Builder.from(CoreReasonCode.UNKNOWN_REASON, messages);

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), reason.getReasonCode().intValue());
        assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), reason.getLabel());
        assertEquals(1, reason.getMessages().size());
        assertEquals("foo", reason.getMessages().toArray()[0]);
    }

    @Test
    public void testFromCoreCodeAndMessages() {
        // Given
        final Builder builder = Builder.from(CoreReasonCode.UNKNOWN_REASON, "foo", "bar");

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), reason.getReasonCode().intValue());
        assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), reason.getLabel());
        assertEquals(2, reason.getMessages().size());
        assertEquals("foo", reason.getMessages().toArray()[0]);
        assertEquals("bar", reason.getMessages().toArray()[1]);
    }

    @Test
    public void testFromKasperReason() {
        // Given
        final Builder builder = Builder.from(new KasperReason(
                "code",
                "foo",
                "bar"
        ));

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals("code", reason.getCode());
        assertEquals(2, reason.getMessages().size());
        assertEquals("foo", reason.getMessages().toArray()[0]);
        assertEquals("bar", reason.getMessages().toArray()[1]);
    }

    @Test
    public void testFromCode() {
        // Given
        final Builder builder = Builder.from(CoreReasonCode.UNKNOWN_REASON.toString());

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), reason.getReasonCode().intValue());
        assertEquals(CoreReasonCode.UNKNOWN_REASON.name(), reason.getLabel());
        assertEquals(0, reason.getMessages().size());
    }

    @Test
    public void testFromCodeAndMessagesCollection() {
        // Given
        final Collection<String> messages = new ArrayList<String>() {{
            add("foo");
        }};
        final Builder builder = Builder.from("code", messages);

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals("code", reason.getCode());
        assertEquals(1, reason.getMessages().size());
        assertEquals("foo", reason.getMessages().toArray()[0]);
    }

    @Test
    public void testFromCodeAndMessages() {
        // Given
        final Builder builder = Builder.from("foo", "foo", "bar");

        // When
        final KasperReason reason = builder.build();

        // Then
        assertEquals("foo", reason.getCode());
        assertEquals(2, reason.getMessages().size());
        assertEquals("foo", reason.getMessages().toArray()[0]);
        assertEquals("bar", reason.getMessages().toArray()[1]);
    }

}
