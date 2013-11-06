package com.viadeo.kasper.security.query;


import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryHandler;

@XKasperQueryHandler(domain = com.viadeo.kasper.security.Security.class)
public class GetIdentityQueryService extends AbstractQueryHandler<GetIdentityQuery, IdentityResponse>{
	@Override
	public QueryResponse<IdentityResponse> retrieve(final GetIdentityQuery query) throws KasperQueryException {
		int memberId = getMemberId();
		return QueryResponse.of(new IdentityResponse(memberId));
	}

	private int getMemberId() {
		return 0;
	}

}
