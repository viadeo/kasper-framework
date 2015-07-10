// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.documentation.XKasperField;
import com.viadeo.kasper.api.domain.query.CollectionQueryResult;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.domain.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.documentation.XKasperQueryResult;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryHandler(domain=Facebook.class)
public class GetAllMemberQueryHandler extends QueryHandler<GetMembersQueryHandler.GetMembersQuery, GetMembersQueryHandler.MembersResult> {

    public static class GetAllMemberQuery implements Query {
        private static final long serialVersionUID = -3449992798518631214L;
    }

    @XKasperQueryResult
    public static class AllMemberResult extends CollectionQueryResult<MemberResult> {
        private static final long serialVersionUID = -8623076962972529773L;
    }

    @XKasperQueryResult
    public static class MemberResult implements QueryResult {
        private static final long serialVersionUID = -4481723019905052067L;
        @XKasperField(description = "the last name of the member")
        public String lastName;
        @XKasperField(description = "the first name of the member")
        public String firstName;
        @XKasperField(description = "the id of the member")
        public KasperID id;
    }
}
