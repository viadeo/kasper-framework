package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;

public class DefaultContextUTest {

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
        Context context = new DefaultContext();

        // When
        Set<String> tags = context.getTags();

        // Then
        assertEquals(newHashSet(), tags);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void setTags_withEmptySetReplacingEmptySet_shouldDoNothing() {
        // Given
        Context context = new DefaultContext();
        Set<String> newTags = newHashSet();

        // When
        Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingEmptySet_shouldSetTags() {
        // Given
        Context context = new DefaultContext();
        Set<String> newTags = newHashSet("a-tag");

        // When
        Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    @Test
    @SuppressWarnings("all")
    public void setTags_withSetReplacingSet_shouldReplaceExistingTags() {
        // Given
        Context context = new DefaultContext();
        Set<String> oldTags = newHashSet("a-tag");
        context = context.setTags(oldTags);

        Set<String> newTags = newHashSet("another-tag");

        // When
        Context newContext = context.setTags(newTags);

        // Then
        assertSame(context, newContext);
        Set<String> actualTags = context.getTags();
        assertEquals(newTags, actualTags);
    }

    // ------------------------------------------------------------------------

    @Test
    public void asMap_withTags_shouldConcatenateThem() {
        Context context = new DefaultContext();
        Set<String> tags = newHashSet("a", "b");
        context = context.setTags(tags);

        // When
        Map<String, String> map = context.asMap(Maps.<String, String>newHashMap());

        // Then
        String serializedTags = map.get(Context.TAGS_SHORTNAME);
        assertTrue(newHashSet("a,b", "b,a").contains(serializedTags)); // order is not important
    }

}
