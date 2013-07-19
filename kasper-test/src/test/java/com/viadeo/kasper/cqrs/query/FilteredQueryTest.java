package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.QueryFilter;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.FilteredQuery;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("serial")
public class FilteredQueryTest extends AbstractPlatformTests {

	// ------------------------------------------------------------------------

	private static final class TestDTO implements QueryDTO {
		private static final long serialVersionUID = -7571158053188747847L;
		private final String name;

		TestDTO(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

	}

	private static final class TestDomain implements Domain { }

	// "*Field" classes are public (so there is no gain in testing accessibility here)
	public static final class TestField<DQO extends QueryDQO<DQO>>
			extends BaseQueryField<String, DQO> {}

	private static final class TestDQO
			extends AbstractQueryDQO<TestDQO> {
		public TestField<TestDQO> name;
	}

	private static final class TestQuery
			extends FilteredQuery<TestDQO> {}

	@XKasperQueryService(domain = TestDomain.class)
	public static final class TestService
			implements QueryService<TestQuery, TestDTO> {
		@Override
		public TestDTO retrieve(final QueryMessage<TestQuery> message) {
			return new TestDTO("the test two");
		}
	}

	// ------------------------------------------------------------------------

	private final QueryGateway servicesGateway;

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
		final QueryFilter<TestDQO> filter =
				dqo.name.filter().endsWith("test").or(
						dqo.name.filter().startsWith("test").and(
								dqo.name.filter().endsWith("two")
						)
				);

		// Then
		assertTrue(filter.isSatisfiedBy(new TestDTO("the test")));
		assertTrue(filter.isSatisfiedBy(new TestDTO("test two")));

		// Given
		final Context context = this.newContext();

		// When
		query.setFilter(filter);

		// Then
		final TestDTO dtoRes = this.servicesGateway.retrieve(query, context);
		assertEquals(dtoRes.getName(), "the test two");
	}

}
