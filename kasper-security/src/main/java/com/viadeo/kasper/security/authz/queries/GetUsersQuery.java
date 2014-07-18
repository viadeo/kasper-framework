package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get users for authorization")
public class GetUsersQuery implements Query {
}
