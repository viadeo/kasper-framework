// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class UnClosableConnectionHandler implements InvocationHandler {

    private static final String CLOSE_METHOD_NAME = "close";
    private Connection underlyingConnection;

    // ------------------------------------------------------------------------

    public UnClosableConnectionHandler(final Connection connection) {
        underlyingConnection = connection;
    }

    // ------------------------------------------------------------------------

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws Throwable {

        if (method.getName().equalsIgnoreCase(CLOSE_METHOD_NAME)) {
            // do nothing
            return null;
        }

        // forward to real object
        return method.invoke(underlyingConnection,args);
    }
    
}
