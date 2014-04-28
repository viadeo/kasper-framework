package com.viadeo.kasper.security.authz;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PermissionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        new Permission(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmpty() {
        new Permission("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlank() {
        new Permission("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnlyDelimiters() {
        new Permission("::,,::,:");
    }

    @Test
    public void testNamed() {
        Permission p1, p2;

        // Case insensitive, same
        p1 = new Permission("something");
        p2 = new Permission("something");
        assertTrue(p1.implies(p2));
        assertTrue(p2.implies(p1));

        // Case insensitive, different case
        p1 = new Permission("something");
        p2 = new Permission("SOMETHING");
        assertTrue(p1.implies(p2));
        assertTrue(p2.implies(p1));

        // Case insensitive, different word
        p1 = new Permission("something");
        p2 = new Permission("else");
        assertFalse(p1.implies(p2));
        assertFalse(p2.implies(p1));
    }

    @Test
    public void testLists() {
        Permission p1, p2, p3;

        p1 = new Permission("one,two");
        p2 = new Permission("one");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new Permission("one,two,three");
        p2 = new Permission("one,three");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new Permission("one,two:one,two,three");
        p2 = new Permission("one:three");
        p3 = new Permission("one:two,three");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));
        assertTrue(p1.implies(p3));
        assertFalse(p2.implies(p3));
        assertTrue(p3.implies(p2));

        p1 = new Permission("one,two,three:one,two,three:one,two");
        p2 = new Permission("one:three:two");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new Permission("one");
        p2 = new Permission("one:two,three,four");
        p3 = new Permission("one:two,three,four:five:six:seven");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertFalse(p2.implies(p1));
        assertFalse(p3.implies(p1));
        assertTrue(p2.implies(p3));

    }

    @Test
    public void testWildcards() {
        Permission p1, p2, p3, p4, p5, p6, p7, p8;

        p1 = new Permission("*");
        p2 = new Permission("one");
        p3 = new Permission("one:two");
        p4 = new Permission("one,two:three,four");
        p5 = new Permission("one,two:three,four,five:six:seven,eight");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));

        p1 = new Permission("newsletter:*");
        p2 = new Permission("newsletter:read");
        p3 = new Permission("newsletter:read,write");
        p4 = new Permission("newsletter:*");
        p5 = new Permission("newsletter:*:*");
        p6 = new Permission("newsletter:*:read");
        p7 = new Permission("newsletter:write:*");
        p8 = new Permission("newsletter:read,write:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));


        p1 = new Permission("newsletter:*:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));

        p1 = new Permission("newsletter:*:*:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));

        p1 = new Permission("newsletter:*:read");
        p2 = new Permission("newsletter:123:read");
        p3 = new Permission("newsletter:123,456:read,write");
        p4 = new Permission("newsletter:read");
        p5 = new Permission("newsletter:read,write");
        p6 = new Permission("newsletter:123:read:write");
        assertTrue(p1.implies(p2));
        assertFalse(p1.implies(p3));
        assertFalse(p1.implies(p4));
        assertFalse(p1.implies(p5));
        assertTrue(p1.implies(p6));

        p1 = new Permission("newsletter:*:read:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p6));

    }
}
