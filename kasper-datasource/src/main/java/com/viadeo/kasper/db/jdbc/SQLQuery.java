package com.viadeo.kasper.db.jdbc;


import com.viadeo.kasper.db.Operation;

public class SQLQuery {

    private Operation operation;
    private String tableName;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
