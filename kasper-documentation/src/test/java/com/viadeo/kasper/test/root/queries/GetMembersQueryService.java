package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryService(domain=Facebook.class)
public class GetMembersQueryService implements QueryService<GetMembersQueryService.GetMembersQuery, GetMembersQueryService.MembersAnswer> {

	public static class GetMembersQuery implements Query {
		private static final long serialVersionUID = -6513893864054353478L;
		public String name;
	}
	
	public static class MembersAnswer implements QueryAnswer {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

	@Override
	public QueryResult<MembersAnswer> retrieve(final QueryMessage<GetMembersQuery> message) throws Exception {
		return null;
	}
	
}
