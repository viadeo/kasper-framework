// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.context;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.MDC;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
public class MDCUtilsUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void enrichMdcContextMap_withNullContext_shouldThrowNPE() {
        // Given
        final Context context = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        MDCUtils.enrichMdcContextMap(context);
    }

    @Test
    public void enrichMdcContextMap_withContext_shouldEnrichMdcContextMapWithIt() {
        // Given
        final Map<String, String> initialContextMap = ImmutableMap.of("foo", "bar");
        MDC.setContextMap(initialContextMap);

        final Context context = Contexts.builder().with("baz", "qux").build();

        // When
        MDCUtils.enrichMdcContextMap(context);

        // Then
        assertEquals(context.asMap(initialContextMap), MDC.getCopyOfContextMap());
    }

}
