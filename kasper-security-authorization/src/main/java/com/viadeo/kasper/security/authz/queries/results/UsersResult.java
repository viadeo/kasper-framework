package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the user authorization list")
public class UsersResult implements QueryResult {

    private final List<UserResult> users;

    public UsersResult(List<UserResult> users) {
        this.users = users;
    }

    public List<UserResult> getUsers() {
        return users;
    }
}
