package com.viadeo.kasper.db.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class UnclosableConnectionProxy implements InvocationHandler {

    private static final String CLOSE_METHOD_NAME = "close";

    private Connection underlyingConnection;

    public UnclosableConnectionProxy(Connection connection) {
        underlyingConnection = connection;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equalsIgnoreCase(CLOSE_METHOD_NAME)) {
            // do nothing
            return null;
        } 
        // forward to real object
        return method.invoke(underlyingConnection,args);
    }

    
}
