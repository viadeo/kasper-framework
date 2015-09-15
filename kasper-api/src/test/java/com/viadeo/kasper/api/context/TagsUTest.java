// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.context;

import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TagsUTest {

    @Test
    @SuppressWarnings("all")
    public void valueOf_withEmptyString_shouldEmptyTags() {
        // Given
        final String string = "";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void valueOf_withATag_shouldReturnTheTag() {
        // Given
        final String string = "a-tag";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldSplitTheTags() {
        // Given
        final String string = "a-tag,another-tag";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldOmitEmptyTags() {
        // Given
        final String string = ",,a-tag,,,,,,,,,another-tag,";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    // ------------------------------------------------------------------------

    @Test
    public void toString_withNoTags_shouldReturnTheEmptyString() {
        // Given
        final Set<String> tags = newHashSet();

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertEquals("", theString);
    }

    @Test
    public void toString_withTags_shouldReturnTheTag() {
        // Given
        final Set<String> tags = newHashSet("a-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertEquals("a-tag", theString);
    }

    @Test
    public void toString_withTags_shouldJoinThem() {
        // Given
        final Set<String> tags = newHashSet("a-tag", "another-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

    @Test
    public void toString_withNullTag_shouldOmitIt() {
        // Given
        final Set<String> tags = newHashSet("a-tag", null, "another-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

}
