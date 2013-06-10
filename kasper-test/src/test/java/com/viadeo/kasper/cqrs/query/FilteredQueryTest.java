package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryFilter;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.FilteredQuery;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("serial")
public class FilteredQueryTest extends AbstractPlatformTests {

	// ------------------------------------------------------------------------

	private static final class TestDTO implements IQueryDTO {
		private static final long serialVersionUID = -7571158053188747847L;
		private final String name;

		TestDTO(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

	}

	private static final class TestDomain implements IDomain {
		@Override
		public String getPrefix() {
			return "tst";
		}
		@Override
		public String getName() {
			return "test";
		}
	}

	// "*Field" classes are public (so there is no gain in testing accessibility here)
	public static final class TestField<DQO extends IQueryDQO<DQO>>
			extends BaseQueryField<String, DQO> {}

	private static final class TestDQO
			extends AbstractQueryDQO<TestDQO> {
		public TestField<TestDQO> name;
	}

	private static final class TestQuery
			extends FilteredQuery<TestDQO> {}

	@XKasperQueryService(domain = TestDomain.class)
	public static final class TestService
			implements IQueryService<TestQuery, TestDTO> {
		@Override
		public TestDTO retrieve(final IQueryMessage<TestQuery> message) {
			return new TestDTO("the test two");
		}
	}

	// ------------------------------------------------------------------------

	private final IQueryGateway servicesGateway;

	public FilteredQueryTest() {
		super();
		this.servicesGateway = this.getPlatform().getQueryGateway();
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() throws Exception {

		// Given
		final TestQuery query = new TestQuery();
		final TestDQO dqo = query.dqo();

		// When
		final IQueryFilter<TestDQO> filter =
				dqo.name.filter().endsWith("test").or(
						dqo.name.filter().startsWith("test").and(
								dqo.name.filter().endsWith("two")
						)
				);

		// Then
		assertTrue(filter.isSatisfiedBy(new TestDTO("the test")));
		assertTrue(filter.isSatisfiedBy(new TestDTO("test two")));

		// Given
		final IContext context = this.newContext();

		// When
		query.setFilter(filter);

		// Then
		final TestDTO dtoRes = this.servicesGateway.retrieve(context, query);
		assertEquals(dtoRes.getName(), "the test two");
	}

}
