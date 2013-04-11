package com.viadeo.kasper.test.cqrs.query;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mockito.Mockito;

import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.impl.QueryFilterNot;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DTOTest;

public class QueryFilterNotTest extends TestCase {

	public void testNot() {
		@SuppressWarnings("unchecked")
		final IQueryFilter<DQOTest> filter = Mockito
		.mock(IQueryFilter.class);
		final DTOTest dto = Mockito.mock(DTOTest.class);

		final QueryFilterNot<DQOTest> notFilter = new QueryFilterNot<DQOTest>(
				filter);

		Mockito.when(filter.isSatisfiedBy(dto)).thenReturn(true);

		Assert.assertFalse(notFilter.isSatisfiedBy(dto));

		try {
			notFilter.isSatisfiedBy(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	public void testConstructNull() {
		try {
			@SuppressWarnings("unused")
			final QueryFilterNot<DQOTest> notFilter = new QueryFilterNot<DQOTest>(
					null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

}
