package com.viadeo.kasper.test.cqrs.query;

import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;
import com.viadeo.kasper.cqrs.query.filter.QueryFilterGroup.Operator;
import com.viadeo.kasper.cqrs.query.filter.impl.QueryFilterGroup;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DTOTest;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;

public class QueryFilterGroupTest extends TestCase {

	@Test
	public void testConstructAndReset() {

		final QueryFilterGroup<DQOTest> groupFilter = new QueryFilterGroup<>();

		Assert.assertEquals(groupFilter.getOperator(), Operator.AND);
		Assert.assertEquals(groupFilter.getFilters().size(), 0);

		groupFilter.reset();

		Assert.assertEquals(groupFilter.getOperator(), Operator.AND);
		Assert.assertEquals(groupFilter.getFilters().size(), 0);

		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> filter1 = Mockito
		.mock(QueryFilter.class);
		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> filter2 = Mockito
		.mock(QueryFilter.class);

		Assert.assertEquals(groupFilter.filter(filter1).filter(filter2)
				.getFilters().size(), 2);

		groupFilter.reset();

		@SuppressWarnings("unchecked")
		final QueryFilterGroup<DQOTest> filter = groupFilter
		.filter(filter1, filter2);
		Assert.assertEquals(filter.getFilters().size(), 2);
	}

	@Test
	public void testNull() {

		final QueryFilterGroup<DQOTest> groupFilter = new QueryFilterGroup<>();

		final DTOTest dto = Mockito.mock(DTOTest.class);
		try {
			groupFilter.isSatisfiedBy(dto);
			Assert.fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		try {
			groupFilter.filter((QueryFilter<DQOTest>) null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> filter = Mockito
		.mock(QueryFilter.class);
		groupFilter.filter(filter);
		try {
			groupFilter.isSatisfiedBy(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	@Test
	public void testBool() {

		final QueryFilterGroup<DQOTest> groupFilter = new QueryFilterGroup<>();
		final DTOTest dqto = Mockito.mock(DTOTest.class);

		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> trueFilter1 = Mockito
		.mock(QueryFilter.class);
		Mockito.when(trueFilter1.isSatisfiedBy(dqto)).thenReturn(true);
		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> trueFilter2 = Mockito
		.mock(QueryFilter.class);
		Mockito.when(trueFilter2.isSatisfiedBy(dqto)).thenReturn(true);
		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> falseFilter1 = Mockito
		.mock(QueryFilter.class);
		Mockito.when(falseFilter1.isSatisfiedBy(dqto)).thenReturn(false);
		@SuppressWarnings("unchecked")
		final QueryFilter<DQOTest> falseFilter2 = Mockito
		.mock(QueryFilter.class);
		Mockito.when(falseFilter2.isSatisfiedBy(dqto)).thenReturn(false);

		Assert.assertTrue(groupFilter.filter(trueFilter1).or()
				.filter(trueFilter2).isSatisfiedBy(dqto));
		Assert.assertTrue(groupFilter.filter(trueFilter1).or(trueFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertTrue(groupFilter.filter(trueFilter1).or()
				.filter(falseFilter2).isSatisfiedBy(dqto));
		Assert.assertTrue(groupFilter.filter(trueFilter1).or(falseFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertFalse(groupFilter.filter(falseFilter1).or()
				.filter(falseFilter2).isSatisfiedBy(dqto));
		Assert.assertFalse(groupFilter.filter(falseFilter1).or(falseFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertTrue(groupFilter.filter(trueFilter1).and()
				.filter(trueFilter2).isSatisfiedBy(dqto));
		Assert.assertTrue(groupFilter.filter(trueFilter1).and(trueFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertFalse(groupFilter.filter(trueFilter1).and()
				.filter(falseFilter2).isSatisfiedBy(dqto));
		Assert.assertFalse(groupFilter.filter(trueFilter1).and(falseFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertFalse(groupFilter.filter(falseFilter1).and()
				.filter(falseFilter2).isSatisfiedBy(dqto));
		Assert.assertFalse(groupFilter.filter(falseFilter1).and(falseFilter2)
				.isSatisfiedBy(dqto));

		groupFilter.reset();

		Assert.assertFalse(groupFilter.filter(falseFilter1).and()
				.filter(trueFilter2).isSatisfiedBy(dqto));
		Assert.assertFalse(groupFilter.filter(falseFilter1).and(trueFilter2)
				.isSatisfiedBy(dqto));

	}

}
