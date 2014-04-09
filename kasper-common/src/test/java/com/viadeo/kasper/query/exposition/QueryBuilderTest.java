// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.google.common.collect.Multimap;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryBuilderTest {

    private QueryBuilder builder;

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String ARRAY = "array";

    // ------------------------------------------------------------------------

    @Before
    public void init() {
        builder = new QueryBuilder();
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleValues() {
        // Given

        // When
        builder.addSingle(KEY, VALUE);

        // Then
        assertEquals(VALUE, builder.first(KEY));
    }

    @Test
    public void testMultipleValues() {

        // Given

        // When
        builder.addSingle(KEY, VALUE).begin(ARRAY).add("1", "2").end();

        // Then
        assertTrue(builder.hasSingle(KEY));
        assertEquals(VALUE, builder.first(KEY));
        assertTrue(builder.values(ARRAY).size() == 2);

        final Iterator<String> it = builder.values(ARRAY).iterator();
        assertEquals("1", it.next());
        assertEquals("2", it.next());
    }

    @Test(expected = IllegalStateException.class)
    public void testBeginBeforeAdd() {
        builder.add("foo bar");
    }

    @Test
    public void testBuild() {

        // Given

        // When
        final Multimap<String, String> map = builder
                .addSingle(KEY, VALUE)
                .begin(ARRAY)
                .add("1", "2")
                .end().build();

        // Then
        assertTrue(map.get(KEY).size() == 1);
        assertEquals(VALUE, map.get(KEY).iterator().next());

        assertTrue(map.get(ARRAY).size() == 2);
        final Iterator<String> it= map.get(ARRAY).iterator();
        assertEquals("1", it.next());
        assertEquals("2", it.next());
    }

    @Test
    public void testComposedCallsToBegin() {
        // Given

        // When
        final Multimap<String, String> map = builder
                .begin("14").add(1)
                .begin("23").add(2).add(3)
                .end()
                .add(4)
                .end().build();

        // Then
        final Iterator<String> it14 = map.get("14").iterator();
        assertEquals("1", it14.next());
        assertEquals("4", it14.next());

        final  Iterator<String> it23 = map.get("23").iterator();
        assertEquals("2", it23.next());
        assertEquals("3", it23.next());
    }

    @Test
    public void testBuildURI() throws URISyntaxException {
        builder.addSingle("name", "fée").begin("names").add("foo", "bar").end();

        assertEquals(
                new URI("http://www.google.com/somepath?name=f%C3%A9e&names=foo&names=bar").toASCIIString(),
                builder.build(new URI("http://www.google.com/somepath")).toASCIIString()
        );
    }

}
