package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the roles authorization list")
public class RolesResult implements QueryResult {

    private final List<RoleResult> roles;

    public RolesResult(List<RoleResult> roles) {
        this.roles = roles;
    }

    public List<RoleResult> getRoles() {
        return roles;
    }
}
