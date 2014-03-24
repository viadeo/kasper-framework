package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryHandler(domain=Facebook.class)
public class GetAllMemberQueryHandler extends QueryHandler<GetMembersQueryHandler.GetMembersQuery, GetMembersQueryHandler.MembersResult> {

    public static class GetAllMemberQuery implements Query {
    }

    @XKasperQueryResult
    public static class AllMemberResult extends CollectionQueryResult<GetMembersQueryHandler.MembersResult> {
    }
}
