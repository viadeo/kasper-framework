package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get groups having the given permission for authorization")
public class GetGroupsHavingPermissionQuery implements Query {

    private final KasperID permissionId;

    public GetGroupsHavingPermissionQuery(final KasperID permissionId) {
        this.permissionId = permissionId;
    }

    public KasperID getPermissionId() {
        return permissionId;
    }
}