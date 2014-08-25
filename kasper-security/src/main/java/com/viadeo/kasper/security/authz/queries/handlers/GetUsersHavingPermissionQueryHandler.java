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
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.queries.GetUsersHavingPermissionQuery;
import com.viadeo.kasper.security.authz.queries.results.UserResult;
import com.viadeo.kasper.security.authz.queries.results.UsersResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's users for a given permission query handler")
public class GetUsersHavingPermissionQueryHandler extends QueryHandler<GetUsersHavingPermissionQuery, UsersResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetUsersHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<UsersResult> retrieve(final QueryMessage<GetUsersHavingPermissionQuery> message) {
        final Optional<List<User>> users = this.authorizationStorage.getUsersHavingPermission(message.getQuery().getPermissionId());
        if (users.isPresent()) {
            return QueryResponse.of(new UsersResult(UserResult.getUserResults(users.get())));
        } else {
            return QueryResponse.error(CoreReasonCode.INVALID_INPUT);
        }
    }
}


