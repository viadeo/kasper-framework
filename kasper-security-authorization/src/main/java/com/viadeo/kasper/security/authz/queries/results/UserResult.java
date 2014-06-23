package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the user authorization info")
public class UserResult implements QueryResult {

    private final String firstName;
    private final String lastName;
    private final KasperID kasperId;
    private final List<RoleResult> roles;
    private final List<PermissionResult> permissions;

    public UserResult(final KasperID kasperId, final String firstName, final String lastName, final List<RoleResult> roles, final List<PermissionResult> permissions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.kasperId = kasperId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
