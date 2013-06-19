package com.viadeo.kasper.test.cqrs.query.base;

import com.viadeo.kasper.cqrs.query.filter.QueryFilter;
import com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup.Operator;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryFilter;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class BaseQueryFilterTest {

	@Test
	public void testOperators() {
		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> filter = Mockito
		.mock(QueryFilter.class);

		@SuppressWarnings("unchecked")
		final AbstractQueryFilter<DQOTest> tested = Mockito.mock(
				AbstractQueryFilter.class, Mockito.CALLS_REAL_METHODS);

		Assert.assertEquals(tested.and(filter).getOperator(), Operator.AND);
		Assert.assertEquals(tested.or(filter).getOperator(), Operator.OR);

		Assert.assertEquals(tested.not().getFilter(), tested);
	}

}
