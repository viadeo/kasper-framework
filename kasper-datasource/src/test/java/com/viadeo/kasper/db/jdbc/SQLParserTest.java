// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.jdbc;

import com.viadeo.kasper.db.Operation;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SQLParserTest {

    @Test
    public void parseShouldHandleSelectQuery() {
        // Given
        final String sql1 = "SELECT nom,prenom,adresse, tel   from toto,tata WHERE age=22;";

        // When
        final SQLQuery query1 =  SQLParser.parse(sql1);

        // Then
        assertEquals(Operation.READ, query1.getOperation());
        assertEquals("toto", query1.getTableName());
    }

    @Test
    public void parseShouldHandleInsertQuery() {
        // Given
        final String sql2 = "INSERT   INTO toto(col1,col2) VALUES (val1, val2)";

        // When
        final SQLQuery query2 =  SQLParser.parse(sql2);

        // Then
        assertEquals(Operation.WRITE, query2.getOperation());
        assertEquals("toto",query2.getTableName());
    }

    @Test
    public void parseShouldHandleUpdateQuery() {
        // Given
        final String sql3 = "UPDATE toto " +
                "SET ContactName='Alfred Schmidt', City='Hamburg' " +
                "WHERE CustomerName='Alfreds Futterkiste';";

        // When
        final SQLQuery query3 =  SQLParser.parse(sql3);

        // Then
        assertEquals(Operation.WRITE, query3.getOperation());
        assertEquals("toto",query3.getTableName());
    }

    @Test
    public void parseShouldHandleDeleteQuery() {
        // Given
        final String sql4 = "DELETE FROM toto "+
                "WHERE CustomerName='Alfreds Futterkiste' AND ContactName='Maria Anders'";

        // When
        final SQLQuery query4 =  SQLParser.parse(sql4);

        // Then
        assertEquals(Operation.WRITE, query4.getOperation());
        assertEquals("toto",query4.getTableName());
    }

}
