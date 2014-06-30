package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get groups having the given user for authorization")
public class GetGroupsHavingUserQuery implements Query {

    private final KasperID userId;

    public GetGroupsHavingUserQuery(final KasperID userId) {
        this.userId = userId;
    }

    public KasperID getUserId() {
        return userId;
    }

}
