package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.queries.GetUserQuery;
import com.viadeo.kasper.security.authz.queries.results.UserResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperQueryHandler(domain = Authorization.class, description = "get authorization's user query handler")
public class GetUserQueryHandler extends QueryHandler<GetUserQuery, UserResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetUserQueryHandler(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    public QueryResponse<UserResult> retrieve(final QueryMessage<GetUserQuery> message) {
        final User user = this.authorizationStorage.getUser(message.getQuery().getUserId());
        return QueryResponse.of(UserResult.getUserResult(user));
    }
}