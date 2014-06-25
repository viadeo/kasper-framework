package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get User having the given role for authorization")
public class GetUsersHavingRoleQuery implements Query {

    private final KasperID roleId;

    public GetUsersHavingRoleQuery(final KasperID roleId) {
        this.roleId = roleId;
    }

    public KasperID getRoleId() {
        return roleId;
    }
}
