package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.queries.GetGroupsQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.queries.results.GroupsResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's groups query handler")
public class GetGroupsQueryHandler extends QueryHandler<GetGroupsQuery, GroupsResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetGroupsQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<GroupsResult> retrieve(final QueryMessage<GetGroupsQuery> message) {
        final List<Group> groups = this.authorizationStorage.getAllGroups();
        return QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups)));
    }
}