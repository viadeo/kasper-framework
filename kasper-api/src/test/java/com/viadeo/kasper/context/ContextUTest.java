// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ContextUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @Test
    public void getFirstIpAddress_withEmpty_shouldReturnAbsent() {
        // Given
        Context context = new Context.Builder().withIpAddress("").build();

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertFalse(ipAddress.isPresent());
    }

    @Test
    public void getFirstIpAddress_withOneIpAddress_shouldReturnIpAddress() {
        // Given
        Context context = new Context.Builder().withIpAddress("127.0.0.1").build();

        // When
        Optional<String> result = context.getFirstIpAddress();

        // Then
        assertEquals("127.0.0.1", result.get());
    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddresses_shouldReturnFirstIpAddress() {
        // Given
        Context context = new Context.Builder()
                .withIpAddress("90.48.246.60, 10.0.1.250, 10.0.7.94, 10.0.1.250")
                .build();

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());
    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddressesWithSpacesEverywhereHihihi_shouldReturnFirstIpAddress() {
        // Given
        Context context = new Context.Builder()
                .withIpAddress("   90.48.246.60   , 10.0.1.250 ,   10.0.7.94, 10.0.1.250                ")
                .build();

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());
    }

    @SuppressWarnings("all")
    public void initialize_byDefault_shouldInitializeTagsAsTheEmptySet() {
        // Given
        final Context context = new Context.Builder().build();

        // When
        final Set<String> tags = context.getTags();

        // Then
        assertEquals(newHashSet(), tags);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void setTags_withEmptySetReplacingEmptySet_shouldDoNothing() {
        // Given
        final Context context = new Context.Builder().build();

        final Set<String> newTags = newHashSet();

        // When
        final Context newContext = context.child().withTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        assertEquals(newTags, context.getTags());
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingEmptySet_shouldSetTags() {
        // Given
        final Context context = new Context.Builder().build();
        final Set<String> newTags = newHashSet("a-tag");

        // When
        final Context newContext = context.child().withTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        assertEquals(newTags, newContext.getTags());
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingSet_shouldReplaceExistingTags() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a-tag"))
                .build();

        final Set<String> newTags = newHashSet("another-tag");

        // When
        final Context newContext = context.child().withTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        final Set<String> actualTags = newContext.getTags();
        assertEquals(newTags, actualTags);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void addTags_withNull_shouldThrowNPE() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a-tag", "another-tag"))
                .build();

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        context.child().addTags(null);
    }

    @Test
    @SuppressWarnings("all")
    public void addTags_withEmptySet_shouldDoNothing() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a-tag", "another-tag"))
                .build();

        final Set<String> newTags = newHashSet();

        // When
        final Context newContext = context.child().addTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

    @Test
    public void addTags_withExistingTags_shouldDoNothing() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a-tag", "another-tag"))
                .build();

        final Set<String> newTags = newHashSet("a-tag");

        // When
        final Context newContext = context.child().addTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        assertEquals(newHashSet("a-tag", "another-tag"), context.getTags());
    }

    @Test
    public void addTags_withNewTags_shouldAddThem() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a-tag", "another-tag"))
                .build();

        final Set<String> newTags = newHashSet("yet-another-tag");

        // When
        final Context newContext = context.child().addTags(newTags).build();

        // Then
        assertNotSame(context, newContext);
        assertEquals(newHashSet("a-tag", "another-tag", "yet-another-tag"), newContext.getTags());
    }

    // ------------------------------------------------------------------------

    @Test
    public void asMap_withTags_shouldConcatenateThem() {
        // Given
        final Context context = new Context.Builder()
                .withTags(newHashSet("a", "b"))
                .build();

        // When
        final Map<String, String> map = context.asMap(Maps.<String, String>newHashMap());

        // Then
        final String serializedTags = map.get(Context.TAGS_SHORTNAME);
        assertTrue(newHashSet("a,b", "b,a").contains(serializedTags)); // order is not important
    }

    @Test
    public void testChild() {
        // Given
        final Context context = new Context.Builder().build();

        // When
        final Context newContext = context.child().build();

        // When
        assertEquals(context.getRequestCorrelationId(), newContext.getRequestCorrelationId());
        assertEquals(context.getSessionCorrelationId(), newContext.getSessionCorrelationId());
        assertEquals(context.getApplicationId(), newContext.getApplicationId());
        assertEquals(context.getUserID(), newContext.getUserID());
        assertEquals(context.getUserLang(), newContext.getUserLang());
        assertEquals(context.getKasperCorrelationId(), newContext.getKasperCorrelationId());
        assertEquals(context.getProperties().size(), newContext.getProperties().size());
        assertEquals(context.getSequence() + 1, newContext.getSequence());
    }

    @Test
    public void testUserLangAsLocale_with_en() {
        // Given
        final Context context = new Context.Builder()
                .withUserLang("en")
                .build();

        // When
        final Locale locale = context.getUserLangAsLocale().get();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage(Locale.ENGLISH));
        assertEquals("English", locale.getDisplayName(Locale.ENGLISH));
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_en_US() {
        // Given
        final Context context = new Context.Builder()
                .withUserLang("en-us")
                .build();

        // When
        final Locale locale = context.getUserLangAsLocale().get();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage(Locale.ENGLISH));
        assertEquals("English (United States)", locale.getDisplayName(Locale.ENGLISH));
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en-US", locale.toLanguageTag());
        assertEquals("US", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_wrong() {
        // Given
        final Context context = new Context.Builder()
                .withUserLang("wrong")
                .build();

        // When
        final Locale locale = context.getUserLangAsLocale().get();

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
