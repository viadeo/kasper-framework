package com.viadeo.kasper.db.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SqlTest {

    @Test
    public void inWithObjectShouldStringSeparatedByComma() {
        String expected = "1,2,3,4,5,6";
        String result = Sql.in(1,2,3,4,5,6);
        assertEquals(expected,result);
    }

    @Test
    public void inWithObjectShouldNotTakeNullsParameters() {
        String expected = "test,tata";
        String result = Sql.in("test",null,"tata");
        assertEquals(expected,result);
    }

    @Test
    public void inWithObjectShouldStringSeparatedByCommaWithoutSpace() {
        String expected = "test,toto,tata";
        String result = Sql.in("test ","toto"," tata ");
        assertEquals(expected,result);
    }

}
