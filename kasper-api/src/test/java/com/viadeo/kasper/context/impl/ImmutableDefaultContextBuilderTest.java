package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.api.TestFormats;
import com.viadeo.kasper.context.ContextValidationException;
import com.viadeo.kasper.context.ImmutableContext;
import com.viadeo.kasper.context.ImmutableContextBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ImmutableDefaultContextBuilderTest {

    ImmutableContextBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new ImmutableDefaultContextBuilder();
    }

    @Test
    public void testBuild_withMissingField_ShouldThrowContextValidationException() throws Exception {

        try {
            builder.build();

            fail("Should be throw ContextValidationException");

        } catch (ContextValidationException e) {
        }
    }

    @Test
    public void testWithUserLang_withBadLang_ShouldThrowValidationException() throws Exception {

        //Given
        ImmutableContextBuilder builder = new ImmutableDefaultContextBuilder()
                .withContext(new ImmutableDefaultContext())
                .withUserID(new ID("viadeo", "member", TestFormats.DB_ID, 42))
                .withUserLang("mouarf");

        //When
        try {
            builder.build();

            fail("Should be throw ContextValidationException");

        } catch (ContextValidationException e) {
        }
    }

    @Test
    public void testWithUserLang_withValidLang_ShouldBeOk() throws Exception {

        //Given
        ImmutableContextBuilder builder = new ImmutableDefaultContextBuilder()
                .withContext(new ImmutableDefaultContext())
                .withUserID(new ID("viadeo", "member", TestFormats.DB_ID, 42))
                .withUserLang("en");

        //When
        builder.build();

    }

    @Test
    public void testWithUserCountry_withValidCountry_ShouldBeOk() throws Exception {

        //Given
        ImmutableContextBuilder builder = new ImmutableDefaultContextBuilder()
                .withContext(new ImmutableDefaultContext())
                .withUserID(new ID("viadeo", "member", TestFormats.DB_ID, 42))
                .withUserCountry("fr");

        //When
        builder.build();
    }

    @Test
    public void testWithUserCountry_withBadCountry_ShouldThrowValidationException() throws Exception {

        //Given
        ImmutableContextBuilder builder = new ImmutableDefaultContextBuilder()
                .withContext(new ImmutableDefaultContext())
                .withUserID(new ID("viadeo", "member", TestFormats.DB_ID, 42))
                .withUserCountry("mouarf");

        //When
        try {
            builder.build();

            fail("Should be throw ContextValidationException");

        } catch (ContextValidationException e) {
        }
    }

    @Test
    public void testChild() {

        // Given
        final ImmutableDefaultContext context = new ImmutableDefaultContext();

        // When
        final ImmutableDefaultContext newContext = (ImmutableDefaultContext) context.child();

        // When
        assertEquals(context.getRequestCorrelationId(), newContext.getRequestCorrelationId());
        assertEquals(context.getSessionCorrelationId(), newContext.getSessionCorrelationId());
        assertEquals(context.getApplicationId(), newContext.getApplicationId());
        assertEquals(context.getUserLang(), newContext.getUserLang());
        assertEquals(context.getKasperCorrelationId(), newContext.getKasperCorrelationId());
        assertEquals(context.getProperties().size(), newContext.getProperties().size());
        assertEquals(context.getSequenceIncrement() + 1, newContext.getSequenceIncrement());
        assertEquals(context, newContext);
    }

    @Test
    public void testUserLangAsLocale_with_en() {

        // Given

        final ImmutableContext context = new ImmutableDefaultContextBuilder()
                .withContext(new ImmutableDefaultContext())
                .withUserLang("en")
                .build();

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage(Locale.ENGLISH));
        assertEquals("English", locale.getDisplayName(Locale.ENGLISH));
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }
}