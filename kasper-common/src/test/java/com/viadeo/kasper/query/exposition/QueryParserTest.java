package com.viadeo.kasper.query.exposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;

public class QueryParserTest {
	@Test
	public void testExists() {
		final QueryParser parser = new QueryParser(ImmutableMap.of("someKey",
				Arrays.asList("foo", "bar")));

		assertTrue(parser.exists("someKey"));
		assertFalse(parser.exists("doesNotExist"));
	}

	@Test
	public void testParseArray() {
		final List<String> expected = Arrays.asList("foo", "bar");
		final QueryParser parser = new QueryParser(ImmutableMap.of("someKey",
				expected));

		final List<String> values = new ArrayList<String>();
		for (QueryParser next : parser.begin("someKey")) {
			values.add(next.value());
		}
		assertEquals("someKey", parser.name());
		parser.end();

		assertFalse(parser.hasNext());
		assertEquals(expected, values);
	}

	@Test(expected=NoSuchElementException.class) public void testNoSuchElementException() {
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
	
	@Test(expected=IllegalStateException.class) public void testIllegalCallEndWithoutBegin() {
		final QueryParser parser = new QueryParser(ImmutableMap.of("someKey", Arrays.asList("foobar")));
		
		parser.end();
	}
}
