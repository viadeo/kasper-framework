// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;

import com.viadeo.kasper.client.lib.QueryBuilder;

import static org.junit.Assert.*;

public class QueryBuilderTest {

	private QueryBuilder builder;

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
        builder.addSingle("key", "value");
        
        // Then
        assertEquals("value", builder.first("key"));
    }

    @Test
    public void testMultipleValues() {
    	
    	// Given
    	
    	// When
        builder.addSingle("key", "value").begin("array").add("1", "2").end();
        
        // Then
        assertTrue(builder.hasSingle("key"));
        assertEquals("value", builder.first("key"));
        assertTrue(builder.values("array").size() == 2);
        assertEquals("1", builder.values("array").get(0));
        assertEquals("2", builder.values("array").get(1));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testBeginBeforeAdd() {
        builder.add("foo bar");
    }
    
    @Test
    public void testBuild() {
    	
    	// Given
    	
    	// When
        final MultivaluedMap<String, String> map = builder.addSingle("key", "value").begin("array").add("1", "2").end().build();
        
        // Then
        assertTrue(map.get("key").size() == 1);
        assertEquals("value", map.getFirst("key"));
        assertTrue(map.get("array").size() == 2);
        assertEquals("1", map.get("array").get(0));
        assertEquals("2", map.get("array").get(1));
    }
}
