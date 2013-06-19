package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryService(domain=Facebook.class)
public class GetMembersQueryService implements QueryService<GetMembersQueryService.Q, GetMembersQueryService.DTO> {

	public static class Q implements Query {
		private static final long serialVersionUID = -6513893864054353478L;
		public String name;
	}
	
	public static class DTO implements QueryDTO {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

	@Override
	public DTO retrieve(final QueryMessage<Q> message) throws Exception {
		return null;
	}
	
}
