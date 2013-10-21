package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryService(domain=Facebook.class)
public class GetMembersQueryService implements QueryService<GetMembersQueryService.Q, GetMembersQueryService.Answer> {

	public static class Q implements Query {
		private static final long serialVersionUID = -6513893864054353478L;
		public String name;
	}
	
	public static class Answer implements QueryAnswer {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

	@Override
	public QueryResult<Answer> retrieve(final QueryMessage<Q> message) throws Exception {
		return null;
	}
	
}
