package com.viadeo.kasper.db.jdbc;

import com.viadeo.kasper.db.Operation;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class SQLParserTest {

    @Test
    public void parseShouldHandleSelectQuery() {
        String sql1 = "SELECT nom,prenom,adresse, tel   from toto,tata WHERE age=22;";
        SQLQuery query1 =  SQLParser.parse(sql1);
        assertEquals(Operation.READ, query1.getOperation());
        assertEquals("toto", query1.getTableName());
    }

    @Test
    public void parseShouldHandleInsertQuery() {
        String sql2 = "INSERT   INTO toto(col1,col2) VALUES (val1, val2)";
        SQLQuery query2 =  SQLParser.parse(sql2);
        assertEquals(Operation.WRITE, query2.getOperation());
        assertEquals("toto",query2.getTableName());
    }

    @Test
    public void parseShouldHandleUpdateQuery() {
        String sql3 = "UPDATE toto " +
                "SET ContactName='Alfred Schmidt', City='Hamburg' " +
                "WHERE CustomerName='Alfreds Futterkiste';";
        SQLQuery query3 =  SQLParser.parse(sql3);
        assertEquals(Operation.WRITE, query3.getOperation());
        assertEquals("toto",query3.getTableName());
    }

    @Test
    public void parseShouldHandleDeleteQuery() {

        String sql4 = "DELETE FROM toto "+
                "WHERE CustomerName='Alfreds Futterkiste' AND ContactName='Maria Anders'";
        SQLQuery query4 =  SQLParser.parse(sql4);
        assertEquals(Operation.WRITE, query4.getOperation());
        assertEquals("toto",query4.getTableName());
    }

}
