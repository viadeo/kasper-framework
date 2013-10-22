// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
