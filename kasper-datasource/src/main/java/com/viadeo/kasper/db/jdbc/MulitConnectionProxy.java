package com.viadeo.kasper.db.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class MulitConnectionProxy implements InvocationHandler {

    private final MultiDataSource ds;
    
    private Connection defaultConnection;

    private static final String METHOD_USING_CONNECTION_PREFIX = "prepare";

    private Connection target = null;

    public MulitConnectionProxy(MultiDataSource commonDs) {
        ds = commonDs;
        defaultConnection = ds.getDefaultConnection();
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // target should be null when there isn't any open connection
    	if (null != target) {
            return method.invoke(target, args);
    	}

        // here we need to create a connection
        if (method.getName().startsWith(METHOD_USING_CONNECTION_PREFIX)) {
            // sql is always the first argument
            // parse the query
            SQLQuery query = SQLParser.parse((String) args[0]);
            target = ds.getConnection(query.getTableName(),query.getOperation());
            // invoke underlying method with the replaced connection
            return method.invoke(target, args);
        } 

        // find something for others cases
        return method.invoke(defaultConnection, args);
    }

    
}
