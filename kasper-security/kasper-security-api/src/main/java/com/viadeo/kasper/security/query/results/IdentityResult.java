package com.viadeo.kasper.security.query.results;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

@XKasperQueryResult
public class IdentityResult implements QueryResult{
    private static final long serialVersionUID = -1639675454899054360L;
    private final int memberId;

    public IdentityResult(final int memberId) {
        this.memberId = memberId;
    }

    public int getIdentity() {
        return this.memberId;
    }
	
}
