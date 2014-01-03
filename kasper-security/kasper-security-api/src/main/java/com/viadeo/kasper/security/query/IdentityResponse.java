package com.viadeo.kasper.security.query;

import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

@XKasperQueryResult
public class IdentityResponse implements QueryResult{ // QueryResponse? But it's not an interface
    private static final long serialVersionUID = -1639675454899054360L;
    private final int memberId;

    public IdentityResponse(final int memberId) {
        this.memberId = memberId;
    }

    public int getIdentity() {
        return this.memberId;
    }
	
}
