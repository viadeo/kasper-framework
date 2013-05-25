package com.viadeo.kasper.test.cqrs.query;

import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.impl.QueryFilterNot;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DTOTest;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class QueryFilterNotTest {

	@Test
	public void testNot() {
		@SuppressWarnings("unchecked")
		final IQueryFilter<DQOTest> filter = Mockito
		.mock(IQueryFilter.class);
		final DTOTest dto = Mockito.mock(DTOTest.class);

		final QueryFilterNot<DQOTest> notFilter = new QueryFilterNot<>(filter);

		Mockito.when(filter.isSatisfiedBy(dto)).thenReturn(true);

		Assert.assertFalse(notFilter.isSatisfiedBy(dto));

		try {
			notFilter.isSatisfiedBy(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	@Test
	public void testConstructNull() {
		try {
			@SuppressWarnings("unused")
			final QueryFilterNot<DQOTest> notFilter = new QueryFilterNot<>(
					null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

}
