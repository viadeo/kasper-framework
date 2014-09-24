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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.queries.GetGroupsHavingUserQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.queries.results.GroupsResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's groups for a given user query handler")
public class GetGroupsHavingUserQueryHandler extends QueryHandler<GetGroupsHavingUserQuery, GroupsResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetGroupsHavingUserQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<GroupsResult> retrieve(final QueryMessage<GetGroupsHavingUserQuery> message) {
        final List<Group> groups = this.authorizationStorage.getGroupsHavingUser(message.getQuery().getUserId());
        return QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups)));
    }
}
