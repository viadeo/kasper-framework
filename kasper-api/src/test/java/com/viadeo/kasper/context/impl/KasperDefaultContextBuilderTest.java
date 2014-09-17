// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.context.Context;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Locale;

public class KasperDefaultContextBuilderTest extends TestCase {

	@Test
	public void testDeterministicId() {
        // Given
		final DefaultContextBuilder builder = new DefaultContextBuilder();

        // When
		final Context context = builder.build();

        // Then
		assertEquals(context.getUserId(), context.getUserId());
	}

    @Test
    public void testChild() {
        // Given
        final DefaultContext context = new DefaultContext();

        // When
        final DefaultContext newContext = (DefaultContext) context.child();

        // When
        assertEquals(context.getRequestCorrelationId(), newContext.getRequestCorrelationId());
        assertEquals(context.getSessionCorrelationId(), newContext.getSessionCorrelationId());
        assertEquals(context.getApplicationId(), newContext.getApplicationId());
        assertEquals(context.getUserId(), newContext.getUserId());
        assertEquals(context.getUserLang(), newContext.getUserLang());
        assertEquals(context.getKasperCorrelationId(), newContext.getKasperCorrelationId());
        assertEquals(context.getProperties().size(), newContext.getProperties().size());
        assertEquals(context.getSequenceIncrement() + 1, newContext.getSequenceIncrement());
        assertEquals(context, newContext);
    }

    @Test
    public void testUserLangAsLocale_with_en() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("en");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage());
        assertEquals("English", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_en_US() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("en-us");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage());
        assertEquals("English (United States)", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en-US", locale.toLanguageTag());
        assertEquals("US", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_wrong() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("wrong");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("wrong", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("wrong", locale.getDisplayLanguage());
        assertEquals("wrong", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("wrong", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }

}
