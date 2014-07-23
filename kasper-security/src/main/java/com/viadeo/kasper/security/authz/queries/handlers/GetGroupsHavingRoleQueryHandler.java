// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.handlers;

import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.queries.GetGroupsHavingRoleQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.queries.results.GroupsResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

@XKasperQueryHandler(domain = Authorization.class, description = "get all authorization's users having a Role query handler")
public class GetGroupsHavingRoleQueryHandler extends QueryHandler<GetGroupsHavingRoleQuery, GroupsResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetGroupsHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<GroupsResult> retrieve(final QueryMessage<GetGroupsHavingRoleQuery> message) {
        final List<Group> groups = this.authorizationStorage.getGroupsHavingRole(message.getQuery().getRoleId());
        return QueryResponse.of(new GroupsResult(GroupResult.getGroupResults(groups)));
    }
}
