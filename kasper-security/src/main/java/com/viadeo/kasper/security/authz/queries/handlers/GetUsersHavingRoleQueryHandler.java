package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.queries.GetUsersHavingRoleQuery;
import com.viadeo.kasper.security.authz.queries.results.UserResult;
import com.viadeo.kasper.security.authz.queries.results.UsersResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's users having a Role query handler")
public class GetUsersHavingRoleQueryHandler extends QueryHandler<GetUsersHavingRoleQuery, UsersResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetUsersHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<UsersResult> retrieve(final QueryMessage<GetUsersHavingRoleQuery> message) {
        final List<User> users = this.authorizationStorage.getUsersHavingRole(message.getQuery().getRoleId());
        return QueryResponse.of(new UsersResult(UserResult.getUserResults(users)));
    }
}

