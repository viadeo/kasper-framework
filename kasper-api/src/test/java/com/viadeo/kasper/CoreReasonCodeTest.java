// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoreReasonCodeTest {

    @Test
    public void shouldStringifyCorrectly() {
        // Given
        final CoreReasonCode code = CoreReasonCode.TOO_MANY_ENTRIES;

        // When
        final String codeString = code.toString();

        // Then
        assertEquals("[1003] - TOO_MANY_ENTRIES", codeString);
        assertEquals(code.reason().getClass(), KasperReason.class);
    }

    @Test
    public void shouldEqualsWithStringCorrectly() {
        // Given
        final CoreReasonCode code = CoreReasonCode.TOO_MANY_ENTRIES;

        // When
        final String codeString = code.toString();

        // Then
        assertTrue(code.equals(codeString));
    }

    @Test
    public void shouldParseKnownCode() {
        // Given
        final CoreReasonCode code = CoreReasonCode.CONFLICT;

        // When
        final CoreReasonCode.ParsedCode parsedCode = CoreReasonCode.parseString(code.toString());

        // Then
        assertEquals(CoreReasonCode.CONFLICT, parsedCode.reason);
    }

    @Test
    public void shouldParseUnknownCode() {
        // Given

        // When
        final CoreReasonCode.ParsedCode parsedCode = CoreReasonCode.parseString("[0000] - TEST");

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), parsedCode.code);
        assertEquals("TEST", parsedCode.label);
    }

    @Test
    public void shouldParseSimpleCode() {
        // Given

        // When
        final CoreReasonCode.ParsedCode parsedCode = CoreReasonCode.parseString("TEST");

        // Then
        assertEquals(CoreReasonCode.UNKNOWN_REASON.code(), parsedCode.code);
        assertEquals("TEST", parsedCode.label);
    }

}

