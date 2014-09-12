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
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.queries.GetPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.PermissionResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

@XKasperQueryHandler(domain = Authorization.class, description = "get authorization's permission query handler")
public class GetPermissionQueryHandler extends QueryHandler<GetPermissionQuery, PermissionResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<PermissionResult> retrieve(final QueryMessage<GetPermissionQuery> message) {
        final Optional<WildcardPermission> permission = this.authorizationStorage.getPermission(message.getQuery().getPermissionId());
        if (permission.isPresent()) {
            return QueryResponse.of(PermissionResult.getPermissionResult(permission.get()));
        } else {
            return QueryResponse.error(CoreReasonCode.NOT_FOUND);
        }
    }
}
