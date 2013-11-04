package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryHandler(domain=Facebook.class)
public class GetMembersQueryHandler implements QueryHandler<GetMembersQueryHandler.GetMembersQuery, GetMembersQueryHandler.MembersResult> {

	public static class GetMembersQuery implements Query {
		private static final long serialVersionUID = -6513893864054353478L;
		public String name;
	}
	
	@XKasperQueryResult
	public static class MembersResult implements QueryResult {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

	@Override
	public QueryResponse<MembersResult> retrieve(final QueryMessage<GetMembersQuery> message) throws Exception {
		return null;
	}
	
}
