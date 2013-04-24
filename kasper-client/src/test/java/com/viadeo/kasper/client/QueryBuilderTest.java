/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryBuilderTest {
    private QueryBuilder builder;

    @Before
    public void init() {
        builder = new QueryBuilder();
    }

    @Test
    public void testSimpleValues() {
        builder.addSingle("key", "value");
        assertEquals("value", builder.first("key"));
    }

    @Test
    public void testMultipleValues() {
        builder.addSingle("key", "value").begin("array").add("1", "2").end();
        assertTrue(builder.hasSingle("key"));
        assertEquals("value", builder.first("key"));
        assertTrue(builder.values("array").size() == 2);
        assertEquals("1", builder.values("array").get(0));
        assertEquals("2", builder.values("array").get(1));
    }
    
    @Test(expected=IllegalStateException.class) public void testBeginBeforeAdd() {
        builder.add("foo bar");
    }
    
    @Test
    public void testBuild() {
        MultivaluedMap<String, String> map = builder.addSingle("key", "value").begin("array").add("1", "2").end().build();
        assertTrue(map.get("key").size() == 1);
        assertEquals("value", map.getFirst("key"));
        assertTrue(map.get("array").size() == 2);
        assertEquals("1", map.get("array").get(0));
        assertEquals("2", map.get("array").get(1));
    }
}
