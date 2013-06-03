// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.jdbc;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.db.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class SQLParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLParser.class);

    public static SQLQuery parse(final String sql) {
        Preconditions.checkNotNull(sql); // fast fail

        final SQLQuery query = new SQLQuery();
        final Scanner scanner = new Scanner(sql.trim());
        final String op = scanner.next("\\w+");

        if ("SELECT".equalsIgnoreCase(op)) {

            query.setOperation(Operation.READ);
            // go to FROM clause
            while (! scanner.hasNext("(?i)FROM")) {
                scanner.next();
            }
            scanner.next(); // go throught the next statement

            final String table = scanner.useDelimiter(",").next();
            query.setTableName(table.trim());

        } else if ("INSERT".equalsIgnoreCase(op)){

            query.setOperation(Operation.WRITE);
            // go to INTO clause
            while (! scanner.hasNext("(?i)INTO")) {
                scanner.next();
            }
            scanner.next(); // go throught the next statement

            final String table = scanner.useDelimiter("[,\\(]").next();
            query.setTableName(table.trim());

        } else if ("UPDATE".equalsIgnoreCase(op)) {

            // easy case
            query.setOperation(Operation.WRITE);
            query.setTableName(scanner.next());

        } else if( "DELETE".equalsIgnoreCase(op)) {

            query.setOperation(Operation.WRITE);
            // go to FROM clause
            scanner.next();

            final String table = scanner.next();
            query.setTableName(table.trim());

        } else {
            LOGGER.error("Error during parsing query:\n{}",sql);
        }

        scanner.close();
        return query;
    }

}
