package com.viadeo.kasper.context;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class TagsUTest {

    @Test
    @SuppressWarnings("all")
    public void valueOf_withEmptyString_shouldEmptyTags() {
        // Given
        String string = "";

        // When
        Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void valueOf_withATag_shouldReturnTheTag() {
        // Given
        String string = "a-tag";

        // When
        Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldSplitTheTags() {
        // Given
        String string = "a-tag,another-tag";

        // When
        Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldOmitEmptyTags() {
        // Given
        String string = ",,a-tag,,,,,,,,,another-tag,";

        // When
        Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    // ------------------------------------------------------------------------

    @Test
    public void toString_withNoTags_shouldReturnTheEmptyString() {
        // Given
        Set<String> tags = newHashSet();

        // When
        String theString = Tags.toString(tags);

        // Then
        assertEquals("", theString);
    }

    @Test
    public void toString_withTags_shouldReturnTheTag() {
        // Given
        Set<String> tags = newHashSet("a-tag");

        // When
        String theString = Tags.toString(tags);

        // Then
        assertEquals("a-tag", theString);
    }

    @Test
    public void toString_withTags_shouldJoinThem() {
        // Given
        Set<String> tags = newHashSet("a-tag", "another-tag");

        // When
        String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

    @Test
    public void toString_withNullTag_shouldOmitIt() {
        // Given
        Set<String> tags = newHashSet("a-tag", null, "another-tag");

        // When
        String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

}
