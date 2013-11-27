package com.viadeo.kasper.client.platform.domain.descriptor;

public class QueryHandlerDescriptor {
    private final Class queryHandlerClass;
    private final Class queryResultClass;
    private final Class queryClass;

    public QueryHandlerDescriptor(Class queryHandlerClass, Class queryClass, Class queryResultClass) {
        this.queryHandlerClass = queryHandlerClass;
        this.queryClass = queryClass;
        this.queryResultClass = queryResultClass;
    }

    public Class getReferenceClass() {
        return queryHandlerClass;
    }

    public Class getQueryResultClass() {
        return queryResultClass;
    }

    public Class getQueryClass() {
        return queryClass;
    }
}
