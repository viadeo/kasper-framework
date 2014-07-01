package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.queries.GetGroupQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

@XKasperQueryHandler(domain = Authorization.class, description = "get authorization's group query handler")
public class GetGroupQueryHandler extends QueryHandler<GetGroupQuery, GroupResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetGroupQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<GroupResult> retrieve(final QueryMessage<GetGroupQuery> message) {
        final Group group = this.authorizationStorage.getGroup(message.getQuery().getGroupId());
        return QueryResponse.of(GroupResult.getGroupResult(group));
    }
}