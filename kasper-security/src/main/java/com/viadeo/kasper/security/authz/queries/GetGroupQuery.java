package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get group for authorization")
public class GetGroupQuery implements Query {

    private final KasperID groupId;

    public GetGroupQuery(KasperID groupId) {
        this.groupId = groupId;
    }

    public KasperID getGroupId() {
        return groupId;
    }
}
