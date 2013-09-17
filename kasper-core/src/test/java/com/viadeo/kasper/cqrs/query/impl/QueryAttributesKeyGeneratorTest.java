package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.cqrs.query.Query;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

public class QueryAttributesKeyGeneratorTest {
    private QueryAttributesKeyGenerator keyGenerator;

    @Before
    public void setUp() {
        keyGenerator = new QueryAttributesKeyGenerator();
    }

    @Test
    public void testStdHashCode() {
        Set<Field> fields = keyGenerator.collectFields(SomeQuery.class, "str", "integer");
        assertIterableEquals(Lists.newArrayList(11, "someStr"), Lists.newArrayList(keyGenerator.collectValues(new SomeQuery(), fields)));
    }

    @Test
    public void testSetDifference() throws NoSuchFieldException {
        Set<String> missingNames = keyGenerator.retainMissingNames(Sets.newHashSet("str", "integer"), Sets.newHashSet(SomeQuery.class.getDeclaredField("str")));
        assertIterableEquals(Lists.newArrayList("integer"), missingNames);
    }

    private void assertIterableEquals(Iterable<?> expected, Iterable<?> actual) {
        Iterator<?> a = actual.iterator();
        for (Object e : expected) {
            assertTrue(a.hasNext());
            assertEquals(e, a.next());
        }
    }

    public static class SomeQuery implements Query {
        public String str = "someStr";
        private int integer = 11;
    }
}
