package com.viadeo.kasper.security.authz.permission;

import com.viadeo.kasper.security.authz.permission.impl.DefaultPermission;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultPermissionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNull_shouldThrowIllegalArgumentException() {
        new DefaultPermission(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmpty_shouldThrowIllegalArgumentException() {
        new DefaultPermission("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlank_shouldThrowIllegalArgumentException() {
        new DefaultPermission("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithOnlyDelimiters_shouldThrowIllegalArgumentException() {
        new DefaultPermission("::,,::,:");
    }

    @Test
    public void testImpliesWithSimpleName_shouldAssertGoodResult() {
        Permission p1, p2;

        // Case insensitive, same
        p1 = new DefaultPermission("something");
        p2 = new DefaultPermission("something");
        assertTrue(p1.implies(p2));
        assertTrue(p2.implies(p1));

        // Case insensitive, different case
        p1 = new DefaultPermission("something");
        p2 = new DefaultPermission("SOMETHING");
        assertTrue(p1.implies(p2));
        assertTrue(p2.implies(p1));

        // Case insensitive, different word
        p1 = new DefaultPermission("something");
        p2 = new DefaultPermission("else");
        assertFalse(p1.implies(p2));
        assertFalse(p2.implies(p1));
    }

    @Test
    public void testImpliesWithLists_shouldAssertGoodResult() {
        Permission p1, p2, p3;

        p1 = new DefaultPermission("one,two");
        p2 = new DefaultPermission("one");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new DefaultPermission("one,two,three");
        p2 = new DefaultPermission("one,three");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new DefaultPermission("one,two:one,two,three");
        p2 = new DefaultPermission("one:three");
        p3 = new DefaultPermission("one:two,three");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));
        assertTrue(p1.implies(p3));
        assertFalse(p2.implies(p3));
        assertTrue(p3.implies(p2));

        p1 = new DefaultPermission("one,two,three:one,two,three:one,two");
        p2 = new DefaultPermission("one:three:two");
        assertTrue(p1.implies(p2));
        assertFalse(p2.implies(p1));

        p1 = new DefaultPermission("one");
        p2 = new DefaultPermission("one:two,three,four");
        p3 = new DefaultPermission("one:two,three,four:five:six:seven");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertFalse(p2.implies(p1));
        assertFalse(p3.implies(p1));
        assertTrue(p2.implies(p3));

    }

    @Test
    public void testImpliesWithComplexePermissions_shouldAssertGoodResult() {
        Permission p1, p2, p3, p4, p5, p6, p7, p8;

        p1 = new DefaultPermission("*");
        p2 = new DefaultPermission("one");
        p3 = new DefaultPermission("one:two");
        p4 = new DefaultPermission("one,two:three,four");
        p5 = new DefaultPermission("one,two:three,four,five:six:seven,eight");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));

        p1 = new DefaultPermission("newsletter:*");
        p2 = new DefaultPermission("newsletter:read");
        p3 = new DefaultPermission("newsletter:read,write");
        p4 = new DefaultPermission("newsletter:*");
        p5 = new DefaultPermission("newsletter:*:*");
        p6 = new DefaultPermission("newsletter:*:read");
        p7 = new DefaultPermission("newsletter:write:*");
        p8 = new DefaultPermission("newsletter:read,write:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));


        p1 = new DefaultPermission("newsletter:*:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));

        p1 = new DefaultPermission("newsletter:*:*:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p3));
        assertTrue(p1.implies(p4));
        assertTrue(p1.implies(p5));
        assertTrue(p1.implies(p6));
        assertTrue(p1.implies(p7));
        assertTrue(p1.implies(p8));

        p1 = new DefaultPermission("newsletter:*:read");
        p2 = new DefaultPermission("newsletter:123:read");
        p3 = new DefaultPermission("newsletter:123,456:read,write");
        p4 = new DefaultPermission("newsletter:read");
        p5 = new DefaultPermission("newsletter:read,write");
        p6 = new DefaultPermission("newsletter:123:read:write");
        assertTrue(p1.implies(p2));
        assertFalse(p1.implies(p3));
        assertFalse(p1.implies(p4));
        assertFalse(p1.implies(p5));
        assertTrue(p1.implies(p6));

        p1 = new DefaultPermission("newsletter:*:read:*");
        assertTrue(p1.implies(p2));
        assertTrue(p1.implies(p6));

    }
}
