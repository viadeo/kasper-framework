package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;

import java.util.List;

@XKasperQueryResult(description = "the role authorization info")
public class RoleResult implements QueryResult {

    private KasperID kasperId;
    private String name;
    private List<PermissionResult> permissions;

    public RoleResult(KasperID kasperId, String name, List<PermissionResult> permissions) {
        this.kasperId = kasperId;
        this.name = name;
        this.permissions = permissions;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public List<PermissionResult> getPermissions() {
        return permissions;
    }

    public String getName() {
        return name;
    }

    public RoleResult getRoleResult(final Role role){
        this.kasperId = role.getEntityId();
        this.name = role.getName();
        for(Permission permission : role.getPermissions()){
            //this.permissions.add(PermissionResult(permission));
        }
        return this;
    }
}
