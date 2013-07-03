// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class QueryParserTest {
    
    @Test public void testConcurrentModificationOfNames() {
        final QueryParser parser = new QueryParser(ImmutableMap.of("key_1", Arrays.asList("a", "b"), "key_2", Arrays.asList("a", "b"), "key2", Arrays.asList("1")));

        for (String name : parser.names()) {
            if (name.startsWith("key_")) {
                for (QueryParser next : parser.begin(name)) {
                    next.value();
                }
                parser.end();
            }
        }
        
        parser.begin("key2").end();
    }

    @Test
    public void testExists() {
        final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", Arrays.asList("foo", "bar")));

        assertTrue(parser.exists("someKey"));
        assertFalse(parser.exists("doesNotExist"));
    }

    @Test
    public void testIterateOverNames() {
        final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", Arrays.asList("foo", "bar")));

        assertTrue(parser.names().contains("someKey"));
    }

    @Test
    public void testParseArray() {
        final List<String> expected = Arrays.asList("foo", "bar");
        final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", expected));

        final List<String> values = new ArrayList<>();
        for (final QueryParser next : parser.begin("someKey")) {
            values.add(next.value());
        }
        assertEquals("someKey", parser.name());
        parser.end();

        assertFalse(parser.hasNext());
        assertEquals(expected, values);
    }

    @Test(expected = NoSuchElementException.class)
    public void testNoSuchElementException() {
        final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", Arrays.asList("foobar")));

        assertTrue(parser.hasNext());

        parser.begin("someKey");
        assertEquals("someKey", parser.name());
        assertTrue(parser.hasNext());

        parser.next();
        assertFalse(parser.hasNext());

        parser.end();
        assertFalse(parser.hasNext());
        parser.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalCallEndWithoutBegin() {
        final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", Arrays.asList("foobar")));
        parser.end();
    }

}
