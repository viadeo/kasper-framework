package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.queries.GetRoleQuery;
import com.viadeo.kasper.security.authz.queries.results.RoleResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

@XKasperQueryHandler(domain = Authorization.class, description = "get authorization's role query handler")
public class GetRoleQueryHandler extends QueryHandler<GetRoleQuery, RoleResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<RoleResult> retrieve(final QueryMessage<GetRoleQuery> message) {
        final Role role = this.authorizationStorage.getRole(message.getQuery().getRoleId());
        return QueryResponse.of(new RoleResult(role));
    }
}
