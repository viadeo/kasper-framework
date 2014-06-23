package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the user authorization list")
public class GroupsResult implements QueryResult {

    private final List<GroupsResult> groups;

    public GroupsResult(List<GroupsResult> groups) {
        this.groups = groups;
    }

    public List<GroupsResult> getGroups() {
        return groups;
    }
}
