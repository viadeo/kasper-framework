// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.cache.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.cqrs.query.Query;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryAttributesKeyGeneratorTest {

    private QueryAttributesKeyGenerator keyGenerator;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        keyGenerator = new QueryAttributesKeyGenerator();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testStdHashCode() {
        // Given
        final Set<Field> fields;

        // When
        fields = keyGenerator.collectFields(SomeQuery.class, "str", "integer");

        // Then
        assertIterableEquals(
                Lists.newArrayList(11, "someStr"),
                Lists.newArrayList(keyGenerator.collectValues(new SomeQuery(), fields))
        );
    }

    @Test
    public void testSetDifference() throws NoSuchFieldException {
        // Given
        final Set<String> missingNames;

        // When
        missingNames = keyGenerator.retainMissingNames(
                Sets.newHashSet("str", "integer"),
                Sets.newHashSet(SomeQuery.class.getDeclaredField("str"))
        );

        // Then
        assertIterableEquals(Lists.newArrayList("integer"), missingNames);
    }

    private void assertIterableEquals(Iterable expected, Iterable actual) {
        // Given
        final Iterator a = actual.iterator();

        // When
        for (final Object e : expected) {

            // Then
            assertTrue(a.hasNext());
            assertEquals(e, a.next());

        }
    }

    // -----------------------------------------------------------------------

    public static class SomeQuery implements Query {
        public String str = "someStr";
        private int integer = 11;
    }

}
