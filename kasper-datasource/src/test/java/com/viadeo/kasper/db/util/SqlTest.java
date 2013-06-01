// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SqlTest {

    @Test
    public void inWithObjectShouldStringSeparatedByComma() {
        // Given
        final String expected = "1,2,3,4,5,6";

        // When
        final String result = Sql.in(1,2,3,4,5,6);

        // Then
        assertEquals(expected,result);
    }

    @Test
    public void inWithObjectShouldNotTakeNullsParameters() {
        // Given
        final String expected = "test,tata";

        // When
        final String result = Sql.in("test",null,"tata");

        // Then
        assertEquals(expected,result);
    }

    @Test
    public void inWithObjectShouldStringSeparatedByCommaWithoutSpace() {
        // Given
        final String expected = "test,toto,tata";

        // When
        final String result = Sql.in("test ","toto"," tata ");

        // Then
        assertEquals(expected,result);
    }

}
