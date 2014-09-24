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
import com.viadeo.kasper.security.authz.queries.GetGroupQuery;
import com.viadeo.kasper.security.authz.queries.results.GroupResult;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

@XKasperQueryHandler(domain = Authorization.class, description = "get authorization's group query handler")
public class GetGroupQueryHandler extends QueryHandler<GetGroupQuery, GroupResult> {

    private final AuthorizationStorage authorizationStorage;

    public GetGroupQueryHandler(AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    public QueryResponse<GroupResult> retrieve(final QueryMessage<GetGroupQuery> message) {
        final Optional<Group> group = this.authorizationStorage.getGroup(message.getQuery().getGroupId());
        if (group.isPresent()) {
            return QueryResponse.of(GroupResult.getGroupResult(group.get()));
        } else {
            return QueryResponse.error(CoreReasonCode.NOT_FOUND);
        }

    }
}
