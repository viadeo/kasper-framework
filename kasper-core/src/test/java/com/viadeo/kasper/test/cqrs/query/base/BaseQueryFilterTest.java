package com.viadeo.kasper.test.cqrs.query.base;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilterGroup.Operator;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryFilter;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;

public class BaseQueryFilterTest extends TestCase {

	@Test
	public void testOperators() {
		@SuppressWarnings("unchecked")
		final IQueryFilter<DQOTest> filter = Mockito
		.mock(IQueryFilter.class);

		@SuppressWarnings("unchecked")
		final AbstractQueryFilter<DQOTest> tested = Mockito.mock(
				AbstractQueryFilter.class, Mockito.CALLS_REAL_METHODS);

		Assert.assertEquals(tested.and(filter).getOperator(), Operator.AND);
		Assert.assertEquals(tested.or(filter).getOperator(), Operator.OR);

		Assert.assertEquals(tested.not().getFilter(), tested);
	}

}
