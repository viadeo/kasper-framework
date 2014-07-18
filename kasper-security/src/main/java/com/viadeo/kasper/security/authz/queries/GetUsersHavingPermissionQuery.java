package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get User having the given permission for authorization")
public class GetUsersHavingPermissionQuery implements Query{

    private final KasperID permissionId;

    public GetUsersHavingPermissionQuery(final KasperID permissionId) {
        this.permissionId = permissionId;
    }

    public KasperID getPermissionId() {
        return permissionId;
    }
}
