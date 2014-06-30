package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.queries.GetUsersQuery;
import com.viadeo.kasper.security.authz.queries.results.UserResult;
import com.viadeo.kasper.security.authz.queries.results.UsersResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's users query handler")
public class GetUsersQueryHandler extends QueryHandler<GetUsersQuery, UsersResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetUsersQueryHandler(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    public QueryResponse<UsersResult> retrieve(final QueryMessage<GetUsersQuery> message) {
        final List<User> users = authorizationStorage.getAllUsers();
        return QueryResponse.of(new UsersResult(UserResult.getUserResults(users)));
    }
}
