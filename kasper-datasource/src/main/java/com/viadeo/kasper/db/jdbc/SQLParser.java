package com.viadeo.kasper.db.jdbc;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.db.Operation;

import java.util.Scanner;


public class SQLParser {

    public static SQLQuery parse(String sql) {
        Preconditions.checkNotNull(sql); // fast fail
        SQLQuery query = new SQLQuery();
        Scanner scanner = new Scanner(sql.trim());
        String op = scanner.next("\\w+");
        if ("SELECT".equalsIgnoreCase(op)) {
            query.setOperation(Operation.READ);
            // go to FROM clause
            while (! scanner.hasNext("(?i)FROM")) {
                scanner.next();
            }
            scanner.next(); // go throught the next statement
            String table = scanner.useDelimiter(",").next();
            query.setTableName(table.trim());
        } else if ("INSERT".equalsIgnoreCase(op)){
            query.setOperation(Operation.WRITE);
            // go to INTO clause
            while (! scanner.hasNext("(?i)INTO")) {
                scanner.next();
            }
            scanner.next(); // go throught the next statement
            String table = scanner.useDelimiter("[,\\(]").next();
            query.setTableName(table.trim());
        } else if ("UPDATE".equalsIgnoreCase(op)) {
            // easy case
            query.setOperation(Operation.WRITE);
            query.setTableName(scanner.next());
        } else {
            // TODO DELETE
        }
        scanner.close();
        return query;
    }


}
