// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.api.domain.query.CollectionQueryResult;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.documentation.XKasperQueryResult;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryHandler(domain=Facebook.class)
public class GetAllMembersQueryHandler extends QueryHandler<GetAllMembersQueryHandler.GetAllMembersQuery, GetAllMembersQueryHandler.AllMembersResult> {

	public static class GetAllMembersQuery implements Query {
		private static final long serialVersionUID = -6513893864054353478L;
	}
	
	@XKasperQueryResult
	public static class AllMembersResult extends CollectionQueryResult<GetMembersQueryHandler.MembersResult> {
		private static final long serialVersionUID = -2174693040511999516L;
	}

}
