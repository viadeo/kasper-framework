// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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
        assertEquals("1", builder.values(ARRAY).get(0));
        assertEquals("2", builder.values(ARRAY).get(1));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testBeginBeforeAdd() {
        builder.add("foo bar");
    }
    
    @Test
    public void testBuild() {
    	
    	// Given
    	
    	// When
        final Map<String, List<String>> map = builder.addSingle(KEY, VALUE).begin(ARRAY).add("1", "2").end().build();
        
        // Then
        assertTrue(map.get(KEY).size() == 1);
        assertEquals(VALUE, map.get(KEY).get(0));
        assertTrue(map.get(ARRAY).size() == 2);
        assertEquals("1", map.get(ARRAY).get(0));
        assertEquals("2", map.get(ARRAY).get(1));
    }
    
    @Test public void testComposedCallsToBegin() {
        // Given
        
        // When
        final Map<String, List<String>> map = builder.begin("14").add(1).begin("23").add(2).add(3).end().add(4).end().build();
        
        // Then
        assertEquals("1", map.get("14").get(0));
        assertEquals("4", map.get("14").get(1));
        assertEquals("2", map.get("23").get(0));
        assertEquals("3", map.get("23").get(1));
    }

}
