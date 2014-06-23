package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

@XKasperQueryResult(description = "the permission authorization info")
public class PermissionResult implements QueryResult {

    private KasperID kasperId;
    private String permission;

    public PermissionResult(KasperID kasperId, String permission) {
        this.kasperId = kasperId;
        this.permission = permission;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public String getPermission() {
        return permission;
    }

    public PermissionResult getPermissionResult(final WildcardPermission permission){
        this.kasperId = permission.getEntityId();
        this.permission = permission.toString();
        return this;
    }
}
