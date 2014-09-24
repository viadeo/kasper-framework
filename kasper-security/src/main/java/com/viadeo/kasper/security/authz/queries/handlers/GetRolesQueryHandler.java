// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.handlers;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.queries.GetRolesQuery;
import com.viadeo.kasper.security.authz.queries.results.RoleResult;
import com.viadeo.kasper.security.authz.queries.results.RolesResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's roles query handler")
public class GetRolesQueryHandler extends QueryHandler<GetRolesQuery, RolesResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetRolesQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<RolesResult> retrieve(final QueryMessage<GetRolesQuery> message) {
        List<Role> roles = this.authorizationStorage.getAllRoles();
        return QueryResponse.of(new RolesResult(RoleResult.getRoleResults(roles)));
    }
}
