package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.queries.GetRolesHavingPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.RoleResult;
import com.viadeo.kasper.security.authz.queries.results.RolesResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's roles for a given permission query handler")
public class GetRolesHavingPermissionQueryHandler extends QueryHandler<GetRolesHavingPermissionQuery, RolesResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetRolesHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<RolesResult> retrieve(final QueryMessage<GetRolesHavingPermissionQuery> message) {
        List<Role> roles = this.authorizationStorage.getRolesHavingPermission(message.getQuery().getPermissionId());
        return QueryResponse.of(new RolesResult(RoleResult.getRoleResults(roles)));
    }
}

