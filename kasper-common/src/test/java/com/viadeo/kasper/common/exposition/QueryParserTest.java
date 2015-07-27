// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.viadeo.kasper.common.exposition.query.QueryParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class QueryParserTest {
    
    @Test
    public void testConcurrentModificationOfNames() {
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                    new ImmutableSetMultimap.Builder<String, String>()
                            .put("key_1", "a")
                            .put("key_1", "b")
                            .put("key_2", "a")
                            .put("key_2", "b")
                            .put("key2", "1")
                            .build()));

        for (final String name : parser.names()) {
            if (name.startsWith("key_")) {
                for (final QueryParser next : parser.begin(name)) {
                    next.value();
                }
                parser.end();
            }
        }
        
        parser.begin("key2").end();
    }

    @Test
    public void testExists() {
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                    new ImmutableSetMultimap.Builder<String, String>()
                        .put("someKey", "foo")
                        .put("someKey", "bar")
                    .build()
                )
        );

        assertTrue(parser.exists("someKey"));
        assertFalse(parser.exists("doesNotExist"));
    }

    @Test
    public void testIterateOverNames() {
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                    new ImmutableSetMultimap.Builder<String, String>()
                            .put("someKey", "foo")
                            .put("someKey", "bar")
                            .build()
                )
        );

        assertTrue(parser.names().contains("someKey"));
    }

    @Test
    public void testParseArray() {
        final List<String> expected = Arrays.asList("foo", "bar");
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                        new ImmutableSetMultimap.Builder<String, String>()
                                .putAll("someKey", expected)
                                .build()
                )
        );

        final List<String> values = new ArrayList<String>();
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
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                    new ImmutableSetMultimap.Builder<String, String>()
                            .putAll("someKey", Arrays.asList("foobar"))
                            .build()
                )
        );

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
        final QueryParser parser = new QueryParser(
                LinkedHashMultimap.create(
                    new ImmutableSetMultimap.Builder<String, String>()
                            .putAll("someKey", Arrays.asList("foobar"))
                            .build()
                )
        );
        parser.end();
    }

}
