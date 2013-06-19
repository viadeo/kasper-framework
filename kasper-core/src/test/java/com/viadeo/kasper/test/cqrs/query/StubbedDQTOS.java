package com.viadeo.kasper.test.cqrs.query;

import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;

public class StubbedDQTOS {

	// ------------------------------------------------------------------------

	public static class DTOTest implements QueryDTO {
		private static final long serialVersionUID = 9038750896453771862L;

	}

	public static class DQOTest extends AbstractQueryDQO<DQOTest> {
		private static final long serialVersionUID = -8117313235876346588L;
	}

	// ------------------------------------------------------------------------

}
