package com.viadeo.kasper.test.cqrs.query;

import java.io.Serializable;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.impl.QueryMessage;
import com.viadeo.kasper.cqrs.query.impl.bean.BeanQuery;

public class BeanQueryTest extends TestCase {

	// ------------------------------------------------------------------------

	private static final class DTOTest implements IQueryDTO {
		private static final long serialVersionUID = -7571158053188742847L;
		private String name;

		public DTOTest setName(final String name) {
			this.name = name;
			return this;
		}

		public String getName() {
			return this.name;
		}

	}

	private static final class TestQueryBean implements Serializable {
		private static final long serialVersionUID = -1768812554558544172L;

		private final String name;

		TestQueryBean(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	private static final class ServiceTest implements IQueryService<BeanQuery<TestQueryBean>, DTOTest> {
		@Override
		public DTOTest retrieve(final IQueryMessage<BeanQuery<TestQueryBean>> message) {
			return new DTOTest().setName(message.getQuery().getBean().get().getName());
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void test() {

		/* == GIVEN == */

		// Will be done by gateway in normal service
		final ServiceTest service = new ServiceTest();

		// Construct a query from a simple bean for the service
		final String name = "name";
		final TestQueryBean bean = new TestQueryBean(name);
		final BeanQuery<TestQueryBean> queryBean = new BeanQuery<TestQueryBean>(bean);

		// Built by gateway in kasper mode
		final IContext context = (new DefaultContextBuilder()).buildDefault();
		final IQueryMessage<BeanQuery<TestQueryBean>> message =
				new QueryMessage<BeanQuery<TestQueryBean>>(context, queryBean);

		/* == WHEN == */
		final DTOTest dto = service.retrieve(message);

		/* == THEN == */
		Assert.assertEquals(name, dto.getName());
	}

}
