// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;

public class DefaultContextUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @Test
    public void getFirstIpAddress_withEmpty_shouldReturnAbsent() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertFalse(ipAddress.isPresent());

    }

    @Test
    public void getFirstIpAddress_withOneIpAddress_shouldReturnIpAddress() {

        // Given
        Context context = new DefaultContext();
        String ipAddress = "127.0.0.1";
        context.setIpAddress(ipAddress);

        // When
        Optional<String> result = context.getFirstIpAddress();

        // Then
        assertEquals(ipAddress, result.get());
    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddresses_shouldReturnFirstIpAddress() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("90.48.246.60, 10.0.1.250, 10.0.7.94, 10.0.1.250");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());

    }

    @Test
    public void getFirstIpAddress_withSeveralIpAddressesWithSpacesEverywhereHihihi_shouldReturnFirstIpAddress() {

        // Given
        Context context = new DefaultContext();
        context.setIpAddress("   90.48.246.60   , 10.0.1.250 ,   10.0.7.94, 10.0.1.250                ");

        // When
        Optional<String> ipAddress = context.getFirstIpAddress();

        // Then
        assertEquals("90.48.246.60", ipAddress.get());

    }

    @SuppressWarnings("all")
    public void constructor_byDefault_shouldInitializeTagsAsTheEmptySet() {
        // Given
        final Context context = new DefaultContext();

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
        final Context context = new DefaultContext();
        final Set<String> newTags = newHashSet();

        // When
        final Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingEmptySet_shouldSetTags() {
        // Given
        final Context context = new DefaultContext();
        final Set<String> newTags = newHashSet("a-tag");

        // When
        final Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingSet_shouldReplaceExistingTags() {
        // Given
        final Context context = new DefaultContext();
        final Set<String> oldTags = newHashSet("a-tag");
        context.setTags(oldTags);

        final Set<String> newTags = newHashSet("another-tag");

        // When
        final Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void addTags_withNull_shouldThrowNPE() {
        // Given
        final Context context = new DefaultContext();
        Set<String> oldTags = newHashSet("a-tag", "another-tag");
        context.setTags(oldTags);

        final Set<String> newTags = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        context.addTags(newTags);
    }

    @Test
    @SuppressWarnings("all")
    public void addTags_withEmptySet_shouldDoNothing() {
        // Given
        final Context context = new DefaultContext();
        final Set<String> oldTags = newHashSet("a-tag", "another-tag");
        context.setTags(oldTags);

        final Set<String> newTags = newHashSet();

        // When
        final Context newContext = context.addTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(oldTags, actualTags);
    }

    @Test
    public void addTags_withExistingTags_shouldDoNothing() {
        // Given
        final Context context = new DefaultContext();
        final Set<String> oldTags = newHashSet("a-tag", "another-tag");
        context.setTags(oldTags);

        final Set<String> newTags = newHashSet("a-tag");

        // When
        final Context newContext = context.addTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(oldTags, actualTags);
    }

    @Test
    public void addTags_withNewTags_shouldAddThem() {
        // Given
        final Context context = new DefaultContext();
        final Set<String> oldTags = newHashSet("a-tag", "another-tag");
        context.setTags(oldTags);

        final Set<String> newTags = newHashSet("yet-another-tag");

        // When
        final Context newContext = context.addTags(newTags);

        // Then
        assertSame(context, newContext);
        final Set<String> actualTags = context.getTags();
        assertEquals(newHashSet("a-tag", "another-tag", "yet-another-tag"), actualTags);
    }

    // ------------------------------------------------------------------------

    @Test
    public void asMap_withTags_shouldConcatenateThem() {
        final Context context = new DefaultContext();
        final Set<String> tags = newHashSet("a", "b");
        context.setTags(tags);

        // When
        final Map<String, String> map = context.asMap(Maps.<String, String>newHashMap());

        // Then
        final String serializedTags = map.get(Context.TAGS_SHORTNAME);
        assertTrue(newHashSet("a,b", "b,a").contains(serializedTags)); // order is not important
    }

}
