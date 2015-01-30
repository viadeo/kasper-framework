package com.viadeo.kasper.cqrs.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.MDC;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.context.Context;

@SuppressWarnings("all")
public class MdcUtilsUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void enrichMdcContextMap_withNullContext_shouldThrowNPE() {
        // Given
        Context context = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        MdcUtils.enrichMdcContextMap(context);
    }

    @Test
    public void enrichMdcContextMap_withContext_shouldEnrichMdcContextMapWithIt() {
        // Given
        Map<String, String> initialContextMap = ImmutableMap.of("foo", "bar");
        MDC.setContextMap(initialContextMap);

        Context context = mock(Context.class);
        Map<String, String> extendedContextMap = ImmutableMap.of("baz", "qux");
        when(context.asMap(anyMap()))
                .thenReturn(extendedContextMap);

        // When
        MdcUtils.enrichMdcContextMap(context);

        // Then
        verify(context)
                .asMap(initialContextMap);
        assertEquals(extendedContextMap, MDC.getCopyOfContextMap());
    }

}
