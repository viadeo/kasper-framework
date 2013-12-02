package com.viadeo.kasper.client.platform.domain.descriptor;

import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;

public class QueryHandlerDescriptor implements Descriptor {
    private final Class<? extends QueryHandler> queryHandlerClass;
    private final Class<? extends QueryResult> queryResultClass;
    private final Class<? extends Query> queryClass;

    public QueryHandlerDescriptor(Class<? extends QueryHandler> queryHandlerClass, Class<? extends Query> queryClass, Class<? extends QueryResult> queryResultClass) {
        this.queryHandlerClass = queryHandlerClass;
        this.queryClass = queryClass;
        this.queryResultClass = queryResultClass;
    }

    @Override
    public Class<? extends QueryHandler> getReferenceClass() {
        return queryHandlerClass;
    }

    public Class<? extends QueryResult> getQueryResultClass() {
        return queryResultClass;
    }

    public Class<? extends Query> getQueryClass() {
        return queryClass;
    }
}
