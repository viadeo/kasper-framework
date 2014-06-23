package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.security.authz.entities.actor.User;

import java.util.List;

@XKasperQueryResult(description = "the group authorization info")
public class GroupResult implements QueryResult {

    private final String name;
    private final List<UserResult> users;
    private final KasperID kasperId;
    private final List<RoleResult> roles;
    private final List<PermissionResult> permissions;


    public GroupResult(final KasperID kasperId, final String name, final List<UserResult> users, final List<RoleResult> roles, final List<PermissionResult> permissions) {
        this.name = name;
        this.users = users;
        this.kasperId = kasperId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<UserResult> getUsers() {
        return users;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public List<RoleResult> getRoles() {
        return roles;
    }

    public List<PermissionResult> getPermissions() {
        return permissions;
    }
}
