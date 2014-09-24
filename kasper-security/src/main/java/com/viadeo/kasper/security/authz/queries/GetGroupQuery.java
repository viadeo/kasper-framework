// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get group for authorization")
public class GetGroupQuery implements Query {

    private final KasperID groupId;

    public GetGroupQuery(final KasperID groupId) {
        this.groupId = groupId;
    }

    public KasperID getGroupId() {
        return groupId;
    }

}
