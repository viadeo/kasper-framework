package com.viadeo.kasper.db.jdbc;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.db.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;


public class SQLParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLParser.class);

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
        } else if( "DELETE".equalsIgnoreCase(op)) {
            query.setOperation(Operation.WRITE);
            // go to FROM clause
                scanner.next();
            String table = scanner.next();
            query.setTableName(table.trim());
        } else {
            LOGGER.error("Error during parsing query:\n{}",sql);
        }
        scanner.close();
        return query;
    }


}
