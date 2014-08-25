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
        final Optional<Role> role = this.authorizationStorage.getRole(message.getQuery().getRoleId());
        if (role.isPresent()) {
            return QueryResponse.of(new RoleResult(role.get()));
        } else {
            return QueryResponse.error(CoreReasonCode.INVALID_INPUT);
        }
    }
}
