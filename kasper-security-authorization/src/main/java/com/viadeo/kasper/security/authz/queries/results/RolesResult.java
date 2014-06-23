package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the roles authorization list")
public class RolesResult implements QueryResult {

    private final List<RolesResult> roles;

    public RolesResult(List<RolesResult> roles) {
        this.roles = roles;
    }

    public List<RolesResult> getRoles() {
        return roles;
    }
}
