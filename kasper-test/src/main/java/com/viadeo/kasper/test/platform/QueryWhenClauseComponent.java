package com.viadeo.kasper.test.platform;

import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.ddd.Entity;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class QueryWhenClauseComponent<CLAUSE extends Clause<CLAUSE>> extends ClauseComponent<CLAUSE> {

    private final Query query;
    private String resultName = "";

    QueryWhenClauseComponent(final CLAUSE clause, final Query query) {
        super(clause);
        this.query = checkNotNull(query);
    }

    public QueryWhenClauseComponent<CLAUSE> as(final String queryName) {
        this.resultName = checkNotNull(queryName);
        return this;
    }

    public Query getQuery() {
        return this.query;
    }

    public String getName() {
        return this.resultName;
    }

}
