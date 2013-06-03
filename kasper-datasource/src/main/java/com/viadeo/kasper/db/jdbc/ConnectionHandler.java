// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class ConnectionHandler implements InvocationHandler {

    private static final String METHOD_USING_CONNECTION_PREFIX = "prepare";

    private final DataSource ds;
    private final Connection defaultConnection;

    private Connection target = null;

    // ------------------------------------------------------------------------

    public ConnectionHandler(final DataSource commonDs) {
        ds = commonDs;
        defaultConnection = ds.getDefaultConnection();
    }

    // ------------------------------------------------------------------------
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        // target should be null when there isn't any open connection
    	if (null != target) {
            return method.invoke(target, args);
    	}

        // here we need to create a connection
        if (method.getName().startsWith(METHOD_USING_CONNECTION_PREFIX)) {
            // sql is always the first argument
            // parse the query
            final SQLQuery query = SQLParser.parse((String) args[0]);
            target = ds.getConnection(query.getTableName(),query.getOperation());

            // invoke underlying method with the replaced connection
            return method.invoke(target, args);
        } 

        // find something for others cases
        return method.invoke(defaultConnection, args);
    }
    
}
