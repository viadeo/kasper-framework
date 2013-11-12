package com.viadeo.kasper.security.query;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get the member security identity")
public class GetIdentityQuery implements Query {
	private static final long serialVersionUID = -9173391734989248143L;
}
