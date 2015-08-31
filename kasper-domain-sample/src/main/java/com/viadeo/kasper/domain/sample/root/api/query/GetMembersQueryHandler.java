// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.api.query;

import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.annotation.XKasperQueryResult;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.validation.AssertID;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.domain.sample.root.api.Facebook;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@XKasperQueryHandler(domain=Facebook.class)
public class GetMembersQueryHandler extends AutowiredQueryHandler<GetMembersQueryHandler.GetMembersQuery, GetMembersQueryHandler.MembersResult> {

    @XKasperAlias(values = "GetUsers")
	public static class GetMembersQuery implements Query {
		private static final long serialVersionUID = -6513893864054353478L;

        @NotNull
        @Min(3)
		public String name;

        @AssertID
        public ID id;

        @AssertID(vendor = "viadeo", type = "member", format = "db-id")
        public ID id2;
	}
	
	@XKasperQueryResult
	public static class MembersResult implements QueryResult {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

}
