package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get groups for authorization")
public class GetGroupsQuery implements Query {
}
