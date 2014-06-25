package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the user authorization list")
public class GroupsResult implements QueryResult {

    private final List<GroupResult> groups;

    public GroupsResult(List<GroupResult> groups) {
        this.groups = groups;
    }

    public List<GroupResult> getGroups() {
        return groups;
    }
}
